import java.util.concurrent.atomic.AtomicInteger;

// to generate student No.. 
public class UniqueIdGenerator {
	static int id;
	private static int lastNumberOfStudent;
	
	AtomicInteger atomicInteger = new AtomicInteger(lastNumberOfStudent);
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
		return lastNumberOfStudent;
	}

	public static void setLastNumberOfStudent(int lastNumberOfStudent) {
		UniqueIdGenerator.lastNumberOfStudent = lastNumberOfStudent;
	}
}
