package be.goossens.oracle.Objects;


public class DBExerciseEvent {

	private long id;
	private String description;
	private int startTime;
	private int endTime;
	private String timeStamp;
	private long exerciseTypeID;
	private long userID;

	public DBExerciseEvent(long id, String description, int startTime,
			int endTime, String timeStamp, long exerciseTypeID, long userID) {
		super();
		this.id = id;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.timeStamp = timeStamp;
		this.exerciseTypeID = exerciseTypeID;
		this.userID = userID;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getExerciseTypeID() {
		return exerciseTypeID;
	}

	public void setExerciseTypeID(long exerciseTypeID) {
		this.exerciseTypeID = exerciseTypeID;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

}
