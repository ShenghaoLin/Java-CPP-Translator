package inputs.homework2;

class Node {
    Node next;
    Object data;

    public Node(Object data) {
        this.next = null;
        this.data = data;
    }
}

public class LinkedList {


    private Node head;

    public LinkedList() {}

    public LinkedList(Node head) {
        this.head = head;
    }

    {
        Node init = null;
        head = init;
    }

    public void add(Node n) {
        Node current = head;

        if (current != null) {
            while (current.next != null) {
                current = current.next;
            }
            current.next = n;
        }
    }

    public boolean remove(int index) {
        if (head == null)
            return false;

        Node current = head;
        for (int i = 0; i < index; i++) {
            if (current.next == null) {
                return false;
            }
            current = current.next;
        }

        {
            Node currentNext = current.next;
            current.next = currentNext.next;
        }

        return true;
    }

}
