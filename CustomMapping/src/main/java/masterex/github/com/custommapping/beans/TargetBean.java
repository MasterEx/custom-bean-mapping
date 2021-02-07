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
package masterex.github.com.custommapping.beans;

import java.util.List;

/**
 *
 * @author Periklis Ntanasis <pntanasis@gmail.com>
 */
public class TargetBean {

    private String stringField;
    private Integer integerField;
    private int integerPrimitiveField;
    private List<String> list;

    // Non encapsulated field
    private String veryPrivateString;

    // Ignored fields
    private String ignoredStringB;

    /**
     * @return the stringField
     */
    public String getStringField() {
        return stringField;
    }

    /**
     * @param stringField the stringField to set
     */
    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    /**
     * @return the integerField
     */
    public Integer getIntegerField() {
        return integerField;
    }

    /**
     * @param integerField the integerField to set
     */
    public void setIntegerField(Integer integerField) {
        this.integerField = integerField;
    }

    /**
     * @return the integerPrimitiveField
     */
    public int getIntegerPrimitiveField() {
        return integerPrimitiveField;
    }

    /**
     * @param integerPrimitiveField the integerPrimitiveField to set
     */
    public void setIntegerPrimitiveField(int integerPrimitiveField) {
        this.integerPrimitiveField = integerPrimitiveField;
    }

    /**
     * @return the list
     */
    public List<String> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<String> list) {
        this.list = list;
    }

    /**
     * @return the ignoredStringB
     */
    public String getIgnoredStringB() {
        return ignoredStringB;
    }

    /**
     * @param ignoredStringB the ignoredStringB to set
     */
    public void setIgnoredStringB(String ignoredStringB) {
        this.ignoredStringB = ignoredStringB;
    }

    @Override
    public String toString() {
        return "TargetBean{" + "stringField=" + stringField + ", integerField=" + integerField + ", integerPrimitiveField=" + integerPrimitiveField + ", list=" + list + ", veryPrivateString=" + veryPrivateString + ", ignoredStringB=" + ignoredStringB + '}';
    }

}
