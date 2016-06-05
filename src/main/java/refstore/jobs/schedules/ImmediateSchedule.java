package refstore.jobs.schedules;

import refstore.jobs.Schedule;

public class ImmediateSchedule extends Schedule {

	@Override
	public boolean isTriggered() {
		return true;
	}
}
