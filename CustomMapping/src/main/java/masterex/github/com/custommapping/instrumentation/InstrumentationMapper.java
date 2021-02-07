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
package masterex.github.com.custommapping.instrumentation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 *
 * @author Periklis Ntanasis <pntanasis@gmail.com>
 * @param <S> Source class to be mapped
 * @param <D> Destination class to be created
 */
public class InstrumentationMapper<S, D> {

    private AbstractMapper<S, D> mapper;

    public InstrumentationMapper(final Class<S> source, final Class<D> destination) {
        try {
            this.mapper = getMapper(source, destination);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("Failed to initialize mapper", ex);
        }
    }

    public D map(S source) {
        return mapper.map(source);
    }

    private synchronized AbstractMapper<S, D> getMapper(final Class<S> source, final Class<D> destination)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException {
        String mapperClassName = (source.getName() + destination.getName()).replaceAll("\\.", "");
        try {
            return (AbstractMapper<S, D>) destination.getClassLoader()
                    .loadClass(mapperClassName).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            return (AbstractMapper<S, D>) generateMapper(destination.getClassLoader(),
                    mapperClassName, source, destination).getDeclaredConstructor().newInstance();
        }
    }

    private Class<?> generateMapper(ClassLoader classLoader, String className, final Class<S> source, final Class<D> destination) {
        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.makeClass(className);
            cc.setInterfaces(new CtClass[]{cp.get(AbstractMapper.class.getName())});
            CtNewConstructor.defaultConstructor(cc);
            CtClass returnType = cp.get(Object.class.getName());
            CtClass[] arguments = new CtClass[]{cp.get(Object.class.getName())};
            CtMethod ctMethod = new CtMethod(returnType, "map", arguments, cc);
            ctMethod.setBody(mappingMethodBody(source, destination));
            cc.addMethod(ctMethod);
            cc.setModifiers(cc.getModifiers() & ~Modifier.ABSTRACT);
            Class<?> generetedClass = cc.toClass();
            return generetedClass;
        } catch (NotFoundException | CannotCompileException ex) {
            Logger.getLogger(InstrumentationMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new RuntimeException("Failed to generate mapper.");
    }

    private String mappingMethodBody(final Class<S> src, final Class<D> destination) {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(String.format("%s dst = new %s();", destination.getName(), destination.getName()));
        sb.append(String.format("%s src = (%s) $1;", src.getName(), src.getName()));
        Method[] srcMethods = src.getDeclaredMethods();
        for (Method srcMethod : srcMethods) {
            if (!srcMethod.getName().startsWith("get")) {
                continue;
            }
            try {
                Method trgMethod = destination.getMethod(srcMethod.getName().replaceFirst("get", "set"), srcMethod.getReturnType());
                sb.append(String.format("dst.%s(src.%s());", trgMethod.getName(), srcMethod.getName()));
            } catch (NoSuchMethodException | SecurityException ex) {
                System.out.println(String.format("Method %s not found or accessibile on source or target class", ex));
            }
        }
        sb.append("return dst;").append("}");
        return sb.toString();
    }

}
