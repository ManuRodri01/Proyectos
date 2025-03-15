package logic;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import logic.Task;
import logic.User;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-03-15T05:32:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(ListTask.class)
public class ListTask_ { 

    public static volatile SingularAttribute<ListTask, Integer> id;
    public static volatile SingularAttribute<ListTask, String> listName;
    public static volatile ListAttribute<ListTask, Task> list;
    public static volatile SingularAttribute<ListTask, User> user;

}