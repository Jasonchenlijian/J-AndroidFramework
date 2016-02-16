package com.clj.jaf.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串辅助类
 */
public class JStringUtil {

    private static final char CHAR_CHINESE_SPACE = '\u3000';//中文（全角）空格

    /**
     * 判断字符串是否为手机号码
     * 只能判断是否为大陆的手机号码
     */
    public static boolean checkMobile(String str) {
        Pattern p = Pattern.compile("1[34578][0-9]{9}");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 验证email的合法性
     */
    public static boolean checkEmail(String emailStr) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(emailStr.trim());
        return matcher.matches();
    }

    /**
     * 对字符串进行MD5加密
     * 如果返回为空，则表示加密失败
     */
    public static String md5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = s.getBytes();
            // 使用MD5创建MessageDigest对象
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte b = md[i];
                // 将每个数(int)b进行双字节加密
                str[k++] = hexDigits[b >> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 去掉字符串的空格
     */
    public static String trim(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(input.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * 从字符串s中截取某一段字符串
     */
    public static String mid(String s, String startToken, String endToken) {
        return mid(s, startToken, endToken, 0);
    }

    public static String mid(String s, String startToken, String endToken, int fromStart) {
        if (startToken == null || endToken == null)
            return null;
        int start = s.indexOf(startToken, fromStart);
        if (start == (-1))
            return null;
        int end = s.indexOf(endToken, start + startToken.length());
        if (end == (-1))
            return null;
        String sub = s.substring(start + startToken.length(), end);
        return sub.trim();
    }

    /**
     * 简化字符串，通过删除空格键、tab键、换行键等实现
     */
    public static String compact(String s) {
        char[] cs = new char[s.length()];
        int len = 0;
        for (int n = 0; n < cs.length; n++) {
            char c = s.charAt(n);
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == CHAR_CHINESE_SPACE)
                continue;
            cs[len] = c;
            len++;
        }
        return new String(cs, 0, len);
    }

    /**
     * 生成uuid
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符数不为空
     */
    public static boolean isNotEmpty(String str) {
        return !JStringUtil.isEmpty(str);
    }

    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     * <p/>
     * <pre>
     * StringHelper.isBlank(null)      = true
     * StringHelper.isBlank("")        = true
     * StringHelper.isBlank(" ")       = true
     * StringHelper.isBlank("bob")     = false
     * StringHelper.isBlank("  bob  ") = false
     * </pre>
     *
     * @param obj the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     */
    public static boolean isBlank(Object obj) {
        if (null == obj) {
            return true;
        }
        String str = obj.toString();
        int strLen;
        if ((strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     * <p/>
     * <pre>
     * StringHelper.isNotBlank(null)      = false
     * StringHelper.isNotBlank("")        = false
     * StringHelper.isNotBlank(" ")       = false
     * StringHelper.isNotBlank("bob")     = true
     * StringHelper.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param obj the String to check, may be not null
     * @return
     */
    public static boolean isNotBlank(Object obj) {
        return !JStringUtil.isBlank(obj);
    }

    public static boolean isNotBlank(Object... objs) {
        if (objs == null || objs.length == 0) {
            return false;
        }

        for (Object obj : objs) {
            if (isNotBlank(obj)) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * 如果str字符串为null,返回为"";如果字符串不为空,返回原来的字符串
     */
    public static String nullToBlank(String str) {
        return (str == null ? "" : str);
    }

    /**
     * 判断某个string是否存在于某个List<String>
     */
    public static boolean existList(String s, ArrayList<String> list) {
        if (list == null)
            return false;

        for (int i = 0; i < list.size(); i++) {
            if (s.equals(list.get(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 截取list成为一个新list
     * begin: 在数组开始在第几个位置
     * end： 在数组中结束在第几个位置
     */
    public static ArrayList<String> cutString(ArrayList<String> list, int begin, int end) {

        ArrayList<String> newList = new ArrayList<>();

        for (int i = begin; i < end; i++) {
            newList.add(list.get(i));
        }

        return newList;
    }

    /**
     * 用捕获异常判断字符串是否是纯数字
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     *
     */


    /**
     * Unicode转String
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }


    /**
     * url中的中文转成编码
     */
    public static String toUtf8String(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = String.valueOf(c).getBytes("utf-8");
                } catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }




    /**
     * 求两个字符串数组的并集，利用set的元素唯一性
     */
    public static String[] union(String[] arr1, String[] arr2) {
        Set<String> set = new HashSet<>();
        for (String str : arr1) {
            set.add(str);
        }
        for (String str : arr2) {
            set.add(str);
        }
        String[] result = {};
        return set.toArray(result);
    }

    /**
     * 求两个字符串数组的交集
     */
    public static String[] intersect(String[] arr1, String[] arr2) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        LinkedList<String> list = new LinkedList<String>();
        for (String str : arr1) {
            if (!map.containsKey(str)) {
                map.put(str, Boolean.FALSE);
            }
        }

        for (String str : arr2) {
            if (map.containsKey(str)) {
                map.put(str, Boolean.TRUE);
            }
        }

        for (Map.Entry<String, Boolean> e : map.entrySet()) {
            if (e.getValue().equals(Boolean.TRUE)) {
                list.add(e.getKey());
            }
        }

        String[] result = {};
        return list.toArray(result);
    }

    /**
     * 求两个字符串数组的差集
     */
    public static String[] minus(String[] arr1, String[] arr2) {
        LinkedList<String> list = new LinkedList<String>();
        LinkedList<String> history = new LinkedList<String>();
        String[] longerArr = arr1;
        String[] shorterArr = arr2;
        //找出较长的数组来减较短的数组
        if (arr1.length > arr2.length) {
            longerArr = arr2;
            shorterArr = arr1;
        }
        for (String str : longerArr) {
            if (!list.contains(str)) {
                list.add(str);
            }
        }
        for (String str : shorterArr) {
            if (list.contains(str)) {
                history.add(str);
                list.remove(str);
            } else {
                if (!history.contains(str)) {
                    list.add(str);
                }
            }
        }

        String[] result = {};
        return list.toArray(result);
    }

    /**
     * 字符串反转
     */
    public static String reverse(String str) {
        return new StringBuffer(str).reverse().toString();
    }

    /**
     * 字符串数组反转
     */
    public static String[] reverse(String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            String top = strs[0];
            for (int j = 1; j < strs.length - i; j++) {
                strs[j - 1] = strs[j];
            }
            strs[strs.length - i - 1] = top;
        }
        return strs;
    }

    /**
     * html的转义字符转换成正常的字符串
     */
    public static String htmlEscapeCharsToString(String html) {
        if (isEmpty(html)) {
            return html;
        } else {
            return html.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
        }
    }

    /**
     * 判断字符串是否为数字,可以判断正数、负数、int、float、double
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isNumber(String str) {
        if (isLong(str)) {
            return true;
        }
        Pattern pattern = Pattern.compile("(-)?(\\d*)\\.{0,1}(\\d*)");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isLong(String str) {
        if ("0".equals(str.trim())) {
            return true;
        }
        Pattern pattern = Pattern.compile("^[^0]\\d*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isFloat(String str) {
        if (isLong(str)) {
            return true;
        }
        Pattern pattern = Pattern.compile("\\d*\\.{1}\\d+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 把List通过编码转换为String
     */
    public static String SceneList2String(List SceneList) throws IOException {

        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(SceneList);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String SceneListString = new String(Base64.encode(
                byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // 关闭objectOutputStream
        objectOutputStream.close();

        return SceneListString;
    }

    /**
     * 把String解码为List
     */
    public static List String2SceneList(String SceneListString)
            throws IOException, ClassNotFoundException {

        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        List SceneList = (List) objectInputStream.readObject();
        objectInputStream.close();

        return SceneList;
    }
}
