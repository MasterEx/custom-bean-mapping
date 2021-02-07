# Java Bean Mapping

This project includes demo code which maps beans using various ways:

 1. Reflection
 2. Code generation
 3. Code instrumentation

It is created only for demonstration purposes. It is not production ready.

In general the performed mapping is based on the field/method names and it is assumed that the source and destination field types are the same.

This demo has been created as a companion to this article: https://masterex.github.io/archive/2021/02/08/java-bean-mapping-in-depth.html

## Outcome

The project contain a few simple main methods that can be invoked in orde to see the mapping in action.

#### masterex.github.com.custommapping.instrumentation.InstrumentationMain

```
Source: SourceBean{stringField=string field!, integerField=1, integerPrimitiveField=2, list=[one, two, three], veryPrivateString=secret, ignoredStringA=ignored}
Target: TargetBean{stringField=string field!, integerField=1, integerPrimitiveField=2, list=[one, two, three], veryPrivateString=null, ignoredStringB=null}
```

#### masterex.github.com.custommapping.reflection.ReflectionMain

```
Source: SourceBean{stringField=string field!, integerField=1, integerPrimitiveField=2, list=[one, two, three], veryPrivateString=secret, ignoredStringA=ignored}
Field ignoredStringA not found on target class
Target: TargetBean{stringField=string field!, integerField=1, integerPrimitiveField=2, list=[one, two, three], veryPrivateString=secret, ignoredStringB=null}
```

#### masterex.github.com.custommapping.staticcodegeneration.StaticCodeGenerationMain

```
Source: SourceBean{stringField=string field!, integerField=1, integerPrimitiveField=2, list=[one, two, three], veryPrivateString=secret, ignoredStringA=ignored}
Target: TargetBean{stringField=string field!, integerField=1, integerPrimitiveField=2, list=[one, two, three], veryPrivateString=null, ignoredStringB=null}
```

## Static Code Generated Mapper

```java
package masterex.github.com.custommapping.staticcodegeneration;

public class StaticCodeGenerationMapperImplementation implements masterex.github.com.custommapping.staticcodegeneration.StaticCodeGenerationMapper {

  @Override
  public masterex.github.com.custommapping.beans.TargetBean map(masterex.github.com.custommapping.beans.SourceBean src) {
    if (src == null) {
      return null;
    }
    masterex.github.com.custommapping.beans.TargetBean dst = new masterex.github.com.custommapping.beans.TargetBean();
    dst.setStringField(src.getStringField());
    dst.setIntegerField(src.getIntegerField());
    dst.setIntegerPrimitiveField(src.getIntegerPrimitiveField());
    dst.setList(src.getList());
    return dst;
  }
}

```
