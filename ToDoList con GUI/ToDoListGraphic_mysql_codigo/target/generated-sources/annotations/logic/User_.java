package logic;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import logic.ListTask;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-03-15T05:32:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, byte[]> image;
    public static volatile SingularAttribute<User, String> password;
    public static volatile ListAttribute<User, ListTask> listOfLists;
    public static volatile SingularAttribute<User, String> username;

}