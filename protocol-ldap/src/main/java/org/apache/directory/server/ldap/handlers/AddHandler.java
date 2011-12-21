/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.server.ldap.handlers;


import org.apache.directory.server.core.api.CoreSession;
import org.apache.directory.server.ldap.LdapSession;
import org.apache.directory.shared.ldap.model.message.AddRequest;
import org.apache.directory.shared.ldap.model.message.LdapResult;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An LDAP add operation {@link AddRequest} handler.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddHandler extends LdapRequestHandler<AddRequest>
{
    /** The logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( AddHandler.class );
    
    
    /**
     * {@inheritDoc}
     */
    public void handle( LdapSession session, AddRequest req ) 
    {
        LOG.debug( "Handling request: {}", req );
        LdapResult result = req.getResultResponse().getLdapResult();
        
        try
        {
            boolean done = false;
            
            do
            {
                txnManager.beginTransaction( false );
                
                try
                {    
                    // Call the underlying layer to inject the new entry
                    CoreSession coreSession = session.getCoreSession();
                    coreSession.add( req );
                }
                catch ( Exception e )
                {
                	txnManager.abortTransaction();
                   
                	handleException( session, req, e );
                }
                
                // If here then we are done.
                done = true;
                
                try
                {
                    txnManager.commitTransaction();
                }
                catch( Exception e )
                {
                	handleException( session, req, e );
                }
            }
            while ( !done );

            // If success, here now, otherwise, we would have an exception.
            result.setResultCode( ResultCodeEnum.SUCCESS );
            
            // Write the AddResponse message
            session.getIoSession().write( req.getResultResponse() );
        }
        catch ( Exception e )
        {
            handleException( session, req, e );
        }
    }
}
