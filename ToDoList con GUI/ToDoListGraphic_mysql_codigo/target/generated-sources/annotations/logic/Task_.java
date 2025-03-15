package logic;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import logic.ListTask;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-03-15T05:32:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Task.class)
public class Task_ { 

    public static volatile SingularAttribute<Task, String> description;
    public static volatile SingularAttribute<Task, Integer> id;
    public static volatile SingularAttribute<Task, ListTask> list;
    public static volatile SingularAttribute<Task, Boolean> isCompleted;

}