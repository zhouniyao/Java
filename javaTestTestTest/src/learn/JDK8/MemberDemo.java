package learn.JDK8;

import java.io.IOException;

public class MemberDemo {

	public static void main(String[] args)throws IOException {
		Member[] members = {
			new Member("A123", "joho", 95),
			new Member("A456", "boduo", 75),	
			new Member("A789", "heimo", 58),	
		};
		
		for (Member member:members) {
			member.save();
		}
		System.out.println(Member.load("A123"));
		System.out.println(Member.load("A456"));
		System.out.println(Member.load("A789"));
		
	}

}
