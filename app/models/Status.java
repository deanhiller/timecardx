package models;

import com.alvazan.orm.api.base.anno.NoSqlEmbeddable;

@NoSqlEmbeddable
public enum Status {
		SUBMIT, APPROVED, CANCELLED;
}
