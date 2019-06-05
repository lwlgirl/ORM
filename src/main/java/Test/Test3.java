package Test;

import annotation.*;

@Table(name = "qwe")
public class Test3 {
    @SQLString()
    public String name;
    @SQLInteger(constraints = @Constraints(unique = true))
    public Integer age;
}
