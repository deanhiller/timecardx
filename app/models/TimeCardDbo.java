package models;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.alvazan.orm.api.base.anno.NoSqlEntity;
import com.alvazan.orm.api.base.anno.NoSqlId;
import com.alvazan.orm.api.base.anno.NoSqlIndexed;
import com.alvazan.orm.api.base.anno.NoSqlManyToOne;
import com.alvazan.orm.api.base.anno.NoSqlOneToMany;

@NoSqlEntity
public class TimeCardDbo {

	private static DateTimeFormatter fmt = DateTimeFormat
			.forPattern("MMM dd, yyyy");

	@NoSqlId(usegenerator = false)
	private String id;

	private DateTime beginOfWeek;

	private int numberOfHours;

	private Status status;
	
	public String getId() {
		return id;
	}

	public void setId(DateTime beginOfWeek) {
		this.beginOfWeek = beginOfWeek;
	}

	public String getRange() {
		DateTime end = beginOfWeek.plusWeeks(1);
		end = end.minusDays(1);
		return fmt.print(beginOfWeek) + " to " + fmt.print(end);
	}

	public DateTime getBeginOfWeek() {
		return beginOfWeek;
	}

	public void setBeginOfWeek(DateTime beginOfWeek) {
		this.beginOfWeek = beginOfWeek;
	}

	public int getNumberOfHours() {
		return numberOfHours;
	}

	public void setNumberOfHours(int numberOfHours) {
		this.numberOfHours = numberOfHours;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
