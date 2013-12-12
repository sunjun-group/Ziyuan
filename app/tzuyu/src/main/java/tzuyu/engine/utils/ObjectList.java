package tzuyu.engine.utils;

import java.util.NoSuchElementException;

public class ObjectList {

  private ObjectList() {
  }

  private static class Node {
    Object data;
    Node next;

    private Node(Object d, Node n) {
      data = d;
      next = n;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof Node) {
        Node n = this;
        Node no = (Node) o;
        for (; n != null && no != null; n = n.next, no = no.next) {
          if (!n.data.equals(no.data)) {
            return false;
          }
        }
        return n == null && no == null;
      } else {
        return false;
      }
    }
  }

  public static class Iterator implements java.util.Iterator<Object>,
      Iterable<Object> {
    Object cur;

    Iterator(Object head) {
      cur = head;
    }

    @Override
    public java.util.Iterator<Object> iterator() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public boolean hasNext() {
      return cur != null;
    }

    @Override
    public Object next() {
      if (cur != null) {
        if (cur instanceof Node) {
          Node n = (Node) cur;
          cur = n.next;
          return n.data;
        } else {// single attribute
          Object n = cur;
          cur = null;
          return n;
        }
      } else {
        throw new NoSuchElementException();
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  static final Iterator emptyIterator = new Iterator(null);

  public static Iterator iterator(Object head) {
    if (head == null) {
      return emptyIterator;
    } else {
      return new Iterator(head);
    }
  }

  public static class TypedIterator<A> implements java.util.Iterator<A>,
      Iterable<A> {
    Object cur;
    Class<A> type;

    TypedIterator(Object head, Class<A> t) {
      cur = head;
      type = t;
      if (head instanceof Node) {
        for (Node n = (Node) head; n != null; n = n.next) {
          if (type.isAssignableFrom(n.data.getClass())) {
            cur = n;
            break;
          }
        }
      } else if (head != null) {
        if (type.isAssignableFrom(head.getClass())) {
          cur = head;
        }
      }
    }

    @Override
    public java.util.Iterator<A> iterator() {
      return null;
    }

    @Override
    public boolean hasNext() {
      return cur != null;
    }

    @Override
    public A next() {
      if (cur != null) {
        if (cur instanceof Node) {
          Node nCur = (Node) cur;
          cur = null;

          @SuppressWarnings("unchecked")
          A d = (A) (nCur.data);

          for (Node n = nCur; n != null; n = n.next) {
            if (type.isAssignableFrom(n.data.getClass())) {
              cur = n;
              break;
            }
          }
          return d;
        } else {// single attribute
          @SuppressWarnings("unchecked")
          A n = (A) cur;
          cur = null;
          return n;
        }
      } else {
        throw new NoSuchElementException();
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  static final TypedIterator<Object> emptyTypedIterator = 
      new TypedIterator<Object>(null, Object.class);

  @SuppressWarnings("unchecked")
  public static <A> TypedIterator<A> typedIterator(Object head, Class<A> type) {
    if (head == null) {
      return (TypedIterator<A>) emptyTypedIterator;
    } else {
      return new TypedIterator<A>(head, type);
    }
  }

  public static boolean equals(Object head1, Object head2) {
    if (head1 != null) {
      return head1.equals(head2);
    } else {
      return head2 == null;
    }
  }

  public static boolean containsType(Object head, Class<?> type) {
    if (head == null || type == null) {
      return false;
    } else if (head instanceof Node) {
      for (Node n = (Node) head; n != null; n = n.next) {
        if (type.isAssignableFrom(n.data.getClass())) {
          return true;
        }
      }
      return false;
    } else {
      return type.isAssignableFrom(head.getClass());
    }
  }

}
