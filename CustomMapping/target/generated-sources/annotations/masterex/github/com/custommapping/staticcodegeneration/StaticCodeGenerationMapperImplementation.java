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
