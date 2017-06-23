package main;
import java.util.*;

/**
 * Created by zhanghr on 2017/4/5.
 */
public class PushPop {
    /**
     * @param pushQ
     * @param popQ
     * @param print
     * @return
     */
    public static boolean push_pop(Queue<Integer> pushQ, Queue<Integer> popQ, boolean print, boolean log, int x, double y){
        Stack<Integer> stack = new Stack<>();
        if (pushQ.size() < popQ.size())
            return false;
        while (!popQ.isEmpty()){
            int pop = popQ.poll();
            while (stack.isEmpty() || stack.peek() != pop) {
                if (pushQ.isEmpty())
                    return false;
                stack.push(pushQ.poll());
                if (print){
                    for (int i = 0; i < stack.size(); i++) {
                        System.out.print(stack.get(i)+"|");
                    }
                    System.out.print("\t<----------- push "+stack.peek()+"\n");
                    System.out.print("===============\n");
                }
            }
            stack.pop();
            if (print){
                for (int i = 0; i < stack.size(); i++) {
                    System.out.print(stack.get(i)+"|");
                }
                System.out.print("\t-----------> pop "+pop+"\n");
                System.out.print("===============\n");
            }
            if (log) {
				System.err.println(x);
			}else {
				System.err.println(y);				
			}
        }
        return true;
    }
    
    public static boolean push_pop(Queue<Integer> pushQ, Queue<Integer> popQ){
        Stack<Integer> stack = new Stack<>();
        if (pushQ.size() < popQ.size())
            return false;
        while (!popQ.isEmpty()){
            int pop = popQ.poll();
            while (stack.isEmpty() || stack.peek() != pop) {
                if (pushQ.isEmpty())
                    return false;
                stack.push(pushQ.poll());
            }
            stack.pop();
        }
        return true;
    }


    static String seperator = " ";
    public static void main(String[] args){
        String push = "6 4 3 2 1", pop = "4 6 3 2 1";
        Queue<Integer> pushQ = createQ(push), popQ = createQ(pop);
        System.out.println(PushPop.push_pop(pushQ, popQ, true, true, 6, 1.1));
    }

    private static Queue<Integer> createQ(String str) {
        Queue<Integer> queue = new LinkedList<>();
        String[] sequence = str.split(seperator);
        for (String integer : sequence) {
            queue.add(Integer.parseInt(integer));
        }
        return queue;
    }
}
