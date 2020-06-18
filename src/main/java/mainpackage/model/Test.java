package mainpackage.model;

public class Test {
    public static void main(String[] args) {
        Note test1 = new Note("111", "swdgfhjjk");
        Note test2 = new Note("222", "fsdfvdsf");
        test2.archiveNote(test2);
        test1.archiveNote(test1);

        System.out.println(test1 + "\n\n" + test2);
    }
}
