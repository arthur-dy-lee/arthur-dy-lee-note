package arthur.dy.lee.array;

/**
 * Created by arthur.dy.lee on 2019-12-02.
 * Merge two sorted linked lists and return it as a new list. The new list should be made by splicing together the nodes of the first two lists.
 * Example:
 * Input: 1->2->4, 1->3->4
 * Output: 1->1->2->3->4->4
 */
public class MergeTwoSortedLists {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (l1 == null)
            return l2;
        if (l2 == null)
            return l1;
        if (l1.val < l2.val) {
            l1.next = mergeTwoLists(l1.next, l2);
            return l1;
        } else {
            l2.next = mergeTwoLists(l1, l2.next);
            return l2;
        }
    }

    public ListNode mergeTwoLists2(ListNode l1, ListNode l2) {
        if (l1 == null && l2 == null) {
            return null;
        }
        if (l1 == null) {
            return l2;
        }
        if (l2 == null) {
            return l1;
        }
        ListNode result = new ListNode(0);
        ListNode prev = result;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                prev.next = l1;
                l1 = l1.next;
            } else {
                prev.next = l2;
                l2 = l2.next;
            }
            prev = prev.next;
        }
        if (l1 != null) {
            prev.next = l1;
        }
        if (l2 != null) {
            prev.next = l2;
        }
        return result.next;
    }

    public static void main(String[] args) {
        ListNode l1_1 = new ListNode(1);
        ListNode l1_2 = new ListNode(2);
        ListNode l1_4 = new ListNode(4);
        l1_1.next = l1_2;
        l1_2.next = l1_4;

        ListNode l2_1 = new ListNode(1);
        ListNode l2_2 = new ListNode(3);
        ListNode l2_4 = new ListNode(4);
        l2_1.next = l2_2;
        l2_2.next = l2_4;

        MergeTwoSortedLists lists = new MergeTwoSortedLists();

        System.out.println(lists.mergeTwoLists(l1_1, l2_1));
    }
}

class ListNode {
    int      val;
    ListNode next;

    public int getVal() {
        return this.val;
    }

    ListNode(int x) {
        val = x;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(val);
        ListNode temp = next;
        while (temp != null && temp.val > 0) {

            s.append(temp.val);
            if (temp.next != null) {
                temp = temp.next;
            } else {
                break;
            }

        }
        return s.toString();
    }

}