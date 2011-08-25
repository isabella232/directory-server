package jdbm.helper;

/**
 * A imple doubly linked list implementation that can be used when fast remove operations are desired.
 * Objects are inserted into the list through an anchor (Link). When object is to be removed from the
 * list, this anchor is provided by the client again and this class can do the remove operation in O(1)
 * using the given anchor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */

public class ExplicitList<T>
{
    
 Link<T> head = new Link<T>( null );   
    
 public static class Link<V>
 {
     private V element;
     private Link<V> next;
     private Link<V> prev;
     
     public Link( V element )
     {
         this.element = element;
         this.reset();
     }
     
     public Link<V> getNext()
     {
         return next;
     }
     
     public void setNext( Link<V> next )
     {
         this.next = next;
     }
     
     public Link<V> getPrev()
     {
         return prev;
     }
     
     public void setPrev( Link<V>  prev )
     {
         this.prev = prev;
     }
     
     public void remove()
     {
         assert( isLinked() );
         this.getPrev().setNext( this.getNext() );
         this.getNext().setPrev( this.getPrev() );
         this.reset();
     }
     
     public void addAfter( Link<V> after )
     {
         after.getNext().setPrev( this );
         this.setNext( after.getNext() );
         after.setNext( this );
         this.setPrev( after );
     }
     
     public void addBefore( Link<V> before )
     {
         before.getPrev().setNext(this );
         this.setPrev( before.getPrev() );
         before.setPrev( this );
         this.setNext( before );
     }
     
     public void splice( Link<V> listHead)
     {
         Link<V> prevLink = listHead.getPrev();
         listHead.setPrev( this );
         prevLink.setNext( this );
         this.setNext( listHead );
         this.setPrev( prevLink );
     }
     
     public boolean isUnLinked()
     {
         return ( prev == this && next == this  );
     }
     
     public boolean isLinked()
     {
         return ( !this.isUnLinked() );
     }
     
     public void reset()
     {
         next = this;
         prev = this;      
     }
     
     public void uninit()
     {
         assert ( this.isUnLinked() );
         element = null;
     }
     
     public V getElement()
     {
         return this.element;
     }
 }
 
 
 public void remove( Link<T> link )
 {
     link.remove();
 }
 
 public void addFirst( Link<T> link )
 {
     link.addAfter( head );
 }
 
 public void addLast( Link<T> link )
 {
    link.addBefore( head );
 }
 
 public Link<T> begin()
 {
     return ( head.getNext() );
 }
 
 public Link<T> end()
 {
     return head;
 }
 
 
}