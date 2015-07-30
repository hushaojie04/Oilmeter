package sj.android.oilmeter;

/**
 * Created by Administrator on 2015/7/30.
 */
public class DoublyLinkedList<T> {
    private Link<T> head;     //首结点
    private Link<T> rear;

    public DoublyLinkedList() {
        head = rear = null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void insertFirst(T data) {
        Link<T> newlink = new Link<T>(data);
        if (isEmpty()) {
            head = newlink;
            head.next = newlink;
            head.previous = null;
            rear = newlink;
            rear.previous = newlink;
            rear.next = null;
        } else {
            //
            newlink.next = head;
            head.previous = newlink;
            head = newlink;
            head.previous = null;
        }
    }

    public void insertLast(T data) {
        Link<T> newlink = new Link<T>(data);
        if (isEmpty()) {
            head.next = newlink;
            head.previous = null;
            rear.previous = newlink;
            rear.next = null;
        } else {
            rear.next = newlink;
            newlink.previous = rear;
            rear = newlink;
            rear.next = null;
        }
    }

    public T deleteHead() throws Exception {
        if (head == null)
            throw new Exception("empty!");
        Link<T> temp = head;
        head = head.next;
        return temp.data;
    }

    public T deleteRear() throws Exception {
        if (rear == null)
            throw new Exception("empty!");
        Link<T> temp = rear;
        rear = rear.previous;
        return temp.data;
    }

    class Link<T> {//链结点
        T data;     //数据域
        Link<T> next; //后继指针，结点           链域
        Link<T> previous; //前驱指针，结点       链域

        Link(T data) {
            this.data = data;
        }

        void displayLink() {
            System.out.println("the data is " + data.toString());
        }
    }
}
