/**
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */
package masterex.github.com.customstaticcodegenerationmapper;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import static masterex.github.com.customstaticcodegenerationmapper.Mappers.SUFFIX;

/**
 *
 * @author Periklis Ntanasis <pntanasis@gmail.com>
 */
@SupportedAnnotationTypes(
        "masterex.github.com.customstaticcodegenerationmapper.Mapper")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class MapperGenerator extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment re) {
        List<Element> annotatedElements = annotations.stream()
                .flatMap(annotation -> re.getElementsAnnotatedWith(annotation).stream())
                .filter(x -> x.getKind() == ElementKind.INTERFACE)
                .collect(Collectors.toList());

        annotatedElements.stream()
                .forEach(this::createImplementation);

        return false;
    }

    private void createImplementation(Element element) {
        String implementationClassName = String.format("%s%s",
                element.getSimpleName().toString(), SUFFIX);
        String interfaceQualifiedName = ((TypeElement) element).getQualifiedName().toString();
        String packageName = interfaceQualifiedName.substring(0, interfaceQualifiedName.lastIndexOf('.'));
        try {
            JavaFileObject mapperFile = processingEnv.getFiler().
                    createSourceFile(packageName + "." + implementationClassName);
            try (PrintWriter out = new PrintWriter(mapperFile.openWriter())) {
                if (packageName != null) {
                    out.print("package ");
                    out.print(packageName);
                    out.println(";");
                    out.println();
                }

                out.print("public class ");
                out.print(implementationClassName);
                out.print(" implements " + interfaceQualifiedName);
                out.println(" {");
                out.println();

                element.getEnclosedElements().stream()
                        .filter(e -> e.getKind() == ElementKind.METHOD)
                        .forEach(e -> generateMethod(e, out));

                out.println("}");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to generate mapper", ex);
        }

    }

    private void generateMethod(Element element, PrintWriter out) {
        TypeMirror srcType = ((ExecutableType) element.asType()).getParameterTypes().get(0);
        TypeMirror destType = ((ExecutableType) element.asType()).getReturnType();

        out.println("  @Override");
        out.println(String.format("  public %s %s(%s src) {", destType, element.getSimpleName(), srcType));
        out.println("    if (src == null) {");
        out.println("      return null;");
        out.println("    }");
        out.println(String.format("    %s dst = new %s();", destType, destType));

        DeclaredType srcDeclaredType = (DeclaredType) srcType;
        List<Element> srcMethods = srcDeclaredType.asElement().getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .filter(e -> e.getSimpleName().toString().startsWith("get"))
                .collect(Collectors.toList());

        DeclaredType destDeclaredType = (DeclaredType) destType;
        Set<String> dstMethods = destDeclaredType.asElement().getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .filter(e -> e.getSimpleName().toString().startsWith("set"))
                .map(e -> e.getSimpleName().toString())
                .collect(Collectors.toSet());

        srcMethods.stream()
                .filter(method -> dstMethods.contains(method.getSimpleName().toString().replaceFirst("g", "s")))
                .forEach(method -> {
                    out.println(String.format("    dst.%s(src.%s);",
                            method.getSimpleName().toString().replaceFirst("g", "s"), method));
                });

        out.println("    return dst;");
        out.println("  }");
    }

}
