import java.util.concurrent.atomic.AtomicInteger;

// to generate student No.. 
public class UniqueIdGenerator {
	static int id;
	private static int lastStudentNumber;
	
	AtomicInteger atomicInteger = new AtomicInteger(lastStudentNumber);
	public UniqueIdGenerator() {
		id = atomicInteger.incrementAndGet();
	}
	
// getters and setters.

	public static int getId() {
		return id;
	}

	public static void setId(int id) {
		UniqueIdGenerator.id = id;
	}

	public static int getLastNumberOfStudent() {
		return lastStudentNumber;
	}

	public static void setLastNumberOfStudent(int lastNumberOfStudent) {
		UniqueIdGenerator.lastStudentNumber = lastNumberOfStudent;
	}
}
