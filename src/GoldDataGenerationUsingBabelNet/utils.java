package GoldDataGenerationUsingBabelNet;

import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.data.BabelPOS;
import it.uniroma1.lcl.babelnet.data.BabelSenseSource;
import utilities.Preprocessing;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Maryam Aminian on 5/2/16.
 */
public class utils {

    private static final Map<Integer, Character> q;
    private static final Map<Integer, Character> r;
    private static final Map<Character, Integer> s;
    private static final Map<Character, Integer> t;
    private static final Map<Character, Character> u;
    private static final Map<Character, Character> v;
    private static final Map<Integer, String> w;
    private static final Map<String, String> x;
    private static final int y;
    private static final int z;

    public static BabelPOS returnBabelNetPOS(String pos) {
        BabelPOS returnPOS = BabelPOS.NOUN;
        if (pos.startsWith("ADJ"))
            returnPOS = BabelPOS.ADJECTIVE;
        else if (pos.endsWith("ADV") || pos.equals("INTERROG_PART") || pos.equals("PART") || pos.equals("FUT_PART") ||
                pos.equals("PART") || pos.equals("PREP"))
            returnPOS = BabelPOS.ADVERB;
        else if (pos.equals("PV") || pos.equals("PSEUDO_VERB"))
            returnPOS = BabelPOS.VERB;

        return returnPOS;
    }

    public static HashSet<String> getTopSensesLemma(List<BabelSense> senses, int num_of_top_senses) {
        int counter = 0;
        HashSet<String> topSenses = new HashSet<String>();
        for (BabelSense sense : senses) {
            if (counter <= num_of_top_senses && (sense.getSource().equals(BabelSenseSource.WNTR) ||
                    sense.getSource().equals(BabelSenseSource.WN))
                    && !topSenses.contains(sense.getLemma())) {
                topSenses.add(sense.getLemma());
                counter++;
            }

        }
        return topSenses;
    }


    public static HashSet<String> getArabicSensesLemma(List<BabelSense> senses) {
        HashSet<String> topSenses = new HashSet<String>();
        for (BabelSense sense : senses) {
            String lemma = sense.getLemma();
            char POS= sense.getPOS().getTag();
            boolean containsUnderScore= lemma.contains("_");
            boolean containsDash= lemma.contains("-");
            boolean hasArabicChars= isArabic(lemma);

            if (!containsUnderScore && !containsDash && hasArabicChars) {

                lemma = Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(convertArabicToBW(lemma)));
                topSenses.add(lemma+"\t"+ POS);
            }
        }
        return topSenses;
    }


    public static HashSet<String> getEnSensesLemma(List<BabelSense> senses) {
        HashSet<String> topSenses = new HashSet<String>();
        for (BabelSense sense : senses) {
            String lemma = sense.getLemma();
            char pos= sense.getPOS().getTag();
            String[] splitLemma =lemma.split("(?=[A-Z])");

            boolean containsUnderScore= lemma.contains("_");
            boolean containsDash= lemma.contains("-");
            boolean containsUpperCaseInMiddle= splitLemma.length > 1 ? true : false;
            boolean containsEnCharacters= lemma.matches("\\w*");

            if (!containsUnderScore && !containsDash && !containsUpperCaseInMiddle && containsEnCharacters)
                topSenses.add(lemma+"\t"+pos);
        }
        return topSenses;
    }


    public static boolean isArabic(String lemma)
    {
        boolean isArabic= true;

        if (lemma.matches("\\w*"))
            isArabic= false;

        return isArabic;
    }

    public static HashSet<String> getEnVocab (String corpus) throws IOException
    {
        HashSet<String> vocab= new HashSet<String>();
        BufferedReader reader= new BufferedReader(new FileReader(corpus));

        String line2read= "";
        while ((line2read= reader.readLine())!= null)
        {
            line2read= line2read.trim();
            String[] splitLine= line2read.split(" ");
            for (String token: splitLine)
            {
                String word= token;
                if (token.contains("@@pos@@"))
                    word= token.split("@@pos@@")[0];
                vocab.add(word);
            }
        }
        System.out.println("Vocabulary size for corpus "+ corpus+":\n"+ vocab.size());
        return vocab;
    }

    public static HashSet<String> getArabicVocab (String corpus) throws IOException
    {
        HashSet<String> vocab= new HashSet<String>();
        BufferedReader reader= new BufferedReader(new FileReader(corpus));

        String line2read= "";
        while ((line2read= reader.readLine())!= null)
        {
            line2read= line2read.trim();
            String[] splitLine= line2read.split(" ");
            for (String token: splitLine)
            {
                String word= token;
                if (token.contains("@@pos@@"))
                    word= token.split("@@pos@@")[0];
                vocab.add(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(word)));
            }
        }
        System.out.println("Vocabulary size for corpus "+ corpus+":\n"+ vocab.size());
        return vocab;
    }

    public static HashSet<String> updateEnVocab (HashSet<String> vocab, String corpus) throws IOException
    {
        BufferedReader reader= new BufferedReader(new FileReader(corpus));

        String line2read= "";
        while ((line2read= reader.readLine())!= null)
        {
            line2read= line2read.trim();
            String[] splitLine= line2read.split(" ");
            for (String token: splitLine)
            {
                String word= token;
                if (token.contains("@@pos@@"))
                    word= token.split("@@pos@@")[0];
                vocab.add(word);
            }
        }
        System.out.println("Vocabulary size for corpus "+ corpus+":\n"+ vocab.size());
        return vocab;
    }

    //implementing part of CCLS utils to convert Arabic characters into BW
    public static String convertArabicToBW(String arabic) {
        StringBuilder var1 = new StringBuilder();
        int var2 = arabic.length();

        int var4;
        for(int var3 = 0; var3 < var2; var3 += Character.charCount(var4)) {
            var4 = arabic.codePointAt(var3);
            Character var5;
            if((var5 = (Character)q.get(Integer.valueOf(var4))) != null) {
                var1.append(var5);
            } else {
                var1.append(arabic.charAt(var3));
            }
        }

        return var1.toString();
    }

    public static InputStream getResourceAsStream(String name) {
        InputStream var2;
        if((var2 = ClassLoader.getSystemClassLoader().getResourceAsStream(name)) != null) {
            return var2;
        } else {
            System.out.println("Unable to load resource from file " + name);
            return var2;
        }
    }

    private static Map<Integer, String> a() {
        HashMap var0 = new HashMap();
        Pattern var1 = Pattern.compile("^u([\\dA-Fa-f]{4})$");
        Pattern var2 = Pattern.compile("^u([\\dA-Fa-f]{4})-u([\\dA-Fa-f]{4})$");
        Pattern var3 = Pattern.compile("u([\\dA-Fa-f]{4})");

        try {
            DataInputStream var4 = new DataInputStream(getResourceAsStream("clean-utf8-MAP"));
            BufferedReader var11 = new BufferedReader(new InputStreamReader(var4));
            String var9 = null;

            String var5;
            while((var5 = var11.readLine()) != null) {
                int var6;
                if((var6 = var5.indexOf(35)) != -1) {
                    var5 = var5.substring(0, var6);
                }

                if(var5.length() > 0) {
                    String[] var12;
                    if((var12 = var5.split("\\t+", 3)).length < 2) {
                        throw new RuntimeException("Found Cleaning Map entry with no action / bad format.");
                    }

                    Integer var7;
                    Matcher var8;
                    Integer var13;
                    if((var8 = var1.matcher(var12[0])).matches()) {
                        var7 = var13 = Integer.valueOf(var8.group(1), 16);
                    } else if((var8 = var2.matcher(var12[0])).matches()) {
                        var13 = Integer.valueOf(var8.group(1), 16);
                        var7 = Integer.valueOf(var8.group(2), 16);
                    } else if(var12[0].equals("INVALID")) {
                        var13 = Integer.valueOf(-2);
                        var7 = Integer.valueOf(-2);
                    } else {
                        if(!var12[0].equals("ELSE")) {
                            throw new RuntimeException("Found Cleaning Map entry bad formatting of first column");
                        }

                        var13 = Integer.valueOf(-1);
                        var7 = Integer.valueOf(-1);
                    }

                    var5 = var12[1];
                    if((var8 = var3.matcher(var5)).find()) {
                        var8.reset();
                        StringBuilder var14 = new StringBuilder();

                        while(var8.find()) {
                            var14.append(Character.toChars(Integer.valueOf(var8.group(1), 16).intValue()));
                        }

                        var9 = var14.toString();
                    } else if(var5.equals("DEL")) {
                        var9 = "";
                    } else if(var5.equals("SPC")) {
                        var9 = " ";
                    } else if(!var5.equals("OK")) {
                        var9 = var5;
                    }

                    for(var6 = var13.intValue(); var6 <= var7.intValue(); ++var6) {
                        if(var5.equals("OK") && var6 >= 0) {
                            var0.put(Integer.valueOf(var6), String.valueOf(Character.toChars(var6)));
                        } else {
                            if(var5.equals("OK") && var6 < 0) {
                                throw new RuntimeException("Bad formatting for INVALID/ELSE command action");
                            }

                            var0.put(Integer.valueOf(var6), var9);
                        }
                    }
                }
            }

            var11.close();
            return var0;
        } catch (IOException var10) {
            throw new RuntimeException("Could not read the Unicode Cleaning Map file during startup");
        }
    }

    static {
        HashMap var0;
        (var0 = new HashMap()).put(Integer.valueOf(1569), Character.valueOf('\''));
        var0.put(Integer.valueOf(1570), Character.valueOf('|'));
        var0.put(Integer.valueOf(1571), Character.valueOf('>'));
        var0.put(Integer.valueOf(1572), Character.valueOf('&'));
        var0.put(Integer.valueOf(1573), Character.valueOf('<'));
        var0.put(Integer.valueOf(1574), Character.valueOf('}'));
        var0.put(Integer.valueOf(1575), Character.valueOf('A'));
        var0.put(Integer.valueOf(1576), Character.valueOf('b'));
        var0.put(Integer.valueOf(1577), Character.valueOf('p'));
        var0.put(Integer.valueOf(1578), Character.valueOf('t'));
        var0.put(Integer.valueOf(1579), Character.valueOf('v'));
        var0.put(Integer.valueOf(1580), Character.valueOf('j'));
        var0.put(Integer.valueOf(1581), Character.valueOf('H'));
        var0.put(Integer.valueOf(1582), Character.valueOf('x'));
        var0.put(Integer.valueOf(1583), Character.valueOf('d'));
        var0.put(Integer.valueOf(1584), Character.valueOf('*'));
        var0.put(Integer.valueOf(1585), Character.valueOf('r'));
        var0.put(Integer.valueOf(1586), Character.valueOf('z'));
        var0.put(Integer.valueOf(1587), Character.valueOf('s'));
        var0.put(Integer.valueOf(1588), Character.valueOf('$'));
        var0.put(Integer.valueOf(1589), Character.valueOf('S'));
        var0.put(Integer.valueOf(1590), Character.valueOf('D'));
        var0.put(Integer.valueOf(1591), Character.valueOf('T'));
        var0.put(Integer.valueOf(1592), Character.valueOf('Z'));
        var0.put(Integer.valueOf(1593), Character.valueOf('E'));
        var0.put(Integer.valueOf(1594), Character.valueOf('g'));
        var0.put(Integer.valueOf(1600), Character.valueOf('_'));
        var0.put(Integer.valueOf(1601), Character.valueOf('f'));
        var0.put(Integer.valueOf(1602), Character.valueOf('q'));
        var0.put(Integer.valueOf(1603), Character.valueOf('k'));
        var0.put(Integer.valueOf(1604), Character.valueOf('l'));
        var0.put(Integer.valueOf(1605), Character.valueOf('m'));
        var0.put(Integer.valueOf(1606), Character.valueOf('n'));
        var0.put(Integer.valueOf(1607), Character.valueOf('h'));
        var0.put(Integer.valueOf(1608), Character.valueOf('w'));
        var0.put(Integer.valueOf(1609), Character.valueOf('Y'));
        var0.put(Integer.valueOf(1610), Character.valueOf('y'));
        var0.put(Integer.valueOf(1611), Character.valueOf('F'));
        var0.put(Integer.valueOf(1612), Character.valueOf('N'));
        var0.put(Integer.valueOf(1613), Character.valueOf('K'));
        var0.put(Integer.valueOf(1614), Character.valueOf('a'));
        var0.put(Integer.valueOf(1615), Character.valueOf('u'));
        var0.put(Integer.valueOf(1616), Character.valueOf('i'));
        var0.put(Integer.valueOf(1617), Character.valueOf('~'));
        var0.put(Integer.valueOf(1618), Character.valueOf('o'));
        var0.put(Integer.valueOf(1648), Character.valueOf('`'));
        var0.put(Integer.valueOf(1649), Character.valueOf('{'));
        var0.put(Integer.valueOf(1662), Character.valueOf('P'));
        var0.put(Integer.valueOf(1670), Character.valueOf('J'));
        var0.put(Integer.valueOf(1700), Character.valueOf('V'));
        var0.put(Integer.valueOf(1711), Character.valueOf('G'));
        q = Collections.unmodifiableMap(var0);
        (var0 = new HashMap()).put(Integer.valueOf(1569), Character.valueOf('C'));
        var0.put(Integer.valueOf(1570), Character.valueOf('M'));
        var0.put(Integer.valueOf(1571), Character.valueOf('O'));
        var0.put(Integer.valueOf(1572), Character.valueOf('W'));
        var0.put(Integer.valueOf(1573), Character.valueOf('I'));
        var0.put(Integer.valueOf(1574), Character.valueOf('Q'));
        var0.put(Integer.valueOf(1575), Character.valueOf('A'));
        var0.put(Integer.valueOf(1576), Character.valueOf('b'));
        var0.put(Integer.valueOf(1577), Character.valueOf('p'));
        var0.put(Integer.valueOf(1578), Character.valueOf('t'));
        var0.put(Integer.valueOf(1579), Character.valueOf('v'));
        var0.put(Integer.valueOf(1580), Character.valueOf('j'));
        var0.put(Integer.valueOf(1581), Character.valueOf('H'));
        var0.put(Integer.valueOf(1582), Character.valueOf('x'));
        var0.put(Integer.valueOf(1583), Character.valueOf('d'));
        var0.put(Integer.valueOf(1584), Character.valueOf('V'));
        var0.put(Integer.valueOf(1585), Character.valueOf('r'));
        var0.put(Integer.valueOf(1586), Character.valueOf('z'));
        var0.put(Integer.valueOf(1587), Character.valueOf('s'));
        var0.put(Integer.valueOf(1588), Character.valueOf('c'));
        var0.put(Integer.valueOf(1589), Character.valueOf('S'));
        var0.put(Integer.valueOf(1590), Character.valueOf('D'));
        var0.put(Integer.valueOf(1591), Character.valueOf('T'));
        var0.put(Integer.valueOf(1592), Character.valueOf('Z'));
        var0.put(Integer.valueOf(1593), Character.valueOf('E'));
        var0.put(Integer.valueOf(1594), Character.valueOf('g'));
        var0.put(Integer.valueOf(1600), Character.valueOf('_'));
        var0.put(Integer.valueOf(1601), Character.valueOf('f'));
        var0.put(Integer.valueOf(1602), Character.valueOf('q'));
        var0.put(Integer.valueOf(1603), Character.valueOf('k'));
        var0.put(Integer.valueOf(1604), Character.valueOf('l'));
        var0.put(Integer.valueOf(1605), Character.valueOf('m'));
        var0.put(Integer.valueOf(1606), Character.valueOf('n'));
        var0.put(Integer.valueOf(1607), Character.valueOf('h'));
        var0.put(Integer.valueOf(1608), Character.valueOf('w'));
        var0.put(Integer.valueOf(1609), Character.valueOf('Y'));
        var0.put(Integer.valueOf(1610), Character.valueOf('y'));
        var0.put(Integer.valueOf(1611), Character.valueOf('F'));
        var0.put(Integer.valueOf(1612), Character.valueOf('N'));
        var0.put(Integer.valueOf(1613), Character.valueOf('K'));
        var0.put(Integer.valueOf(1614), Character.valueOf('a'));
        var0.put(Integer.valueOf(1615), Character.valueOf('u'));
        var0.put(Integer.valueOf(1616), Character.valueOf('i'));
        var0.put(Integer.valueOf(1617), Character.valueOf('X'));
        var0.put(Integer.valueOf(1618), Character.valueOf('o'));
        var0.put(Integer.valueOf(1648), Character.valueOf('e'));
        var0.put(Integer.valueOf(1649), Character.valueOf('L'));
        var0.put(Integer.valueOf(1662), Character.valueOf('P'));
        var0.put(Integer.valueOf(1670), Character.valueOf('J'));
        var0.put(Integer.valueOf(1700), Character.valueOf('B'));
        var0.put(Integer.valueOf(1711), Character.valueOf('G'));
        r = Collections.unmodifiableMap(var0);
        (var0 = new HashMap()).put(Character.valueOf('\''), Integer.valueOf(1569));
        var0.put(Character.valueOf('|'), Integer.valueOf(1570));
        var0.put(Character.valueOf('>'), Integer.valueOf(1571));
        var0.put(Character.valueOf('&'), Integer.valueOf(1572));
        var0.put(Character.valueOf('<'), Integer.valueOf(1573));
        var0.put(Character.valueOf('}'), Integer.valueOf(1574));
        var0.put(Character.valueOf('A'), Integer.valueOf(1575));
        var0.put(Character.valueOf('b'), Integer.valueOf(1576));
        var0.put(Character.valueOf('p'), Integer.valueOf(1577));
        var0.put(Character.valueOf('t'), Integer.valueOf(1578));
        var0.put(Character.valueOf('v'), Integer.valueOf(1579));
        var0.put(Character.valueOf('j'), Integer.valueOf(1580));
        var0.put(Character.valueOf('H'), Integer.valueOf(1581));
        var0.put(Character.valueOf('x'), Integer.valueOf(1582));
        var0.put(Character.valueOf('d'), Integer.valueOf(1583));
        var0.put(Character.valueOf('*'), Integer.valueOf(1584));
        var0.put(Character.valueOf('r'), Integer.valueOf(1585));
        var0.put(Character.valueOf('z'), Integer.valueOf(1586));
        var0.put(Character.valueOf('s'), Integer.valueOf(1587));
        var0.put(Character.valueOf('$'), Integer.valueOf(1588));
        var0.put(Character.valueOf('S'), Integer.valueOf(1589));
        var0.put(Character.valueOf('D'), Integer.valueOf(1590));
        var0.put(Character.valueOf('T'), Integer.valueOf(1591));
        var0.put(Character.valueOf('Z'), Integer.valueOf(1592));
        var0.put(Character.valueOf('E'), Integer.valueOf(1593));
        var0.put(Character.valueOf('g'), Integer.valueOf(1594));
        var0.put(Character.valueOf('_'), Integer.valueOf(1600));
        var0.put(Character.valueOf('f'), Integer.valueOf(1601));
        var0.put(Character.valueOf('q'), Integer.valueOf(1602));
        var0.put(Character.valueOf('k'), Integer.valueOf(1603));
        var0.put(Character.valueOf('l'), Integer.valueOf(1604));
        var0.put(Character.valueOf('m'), Integer.valueOf(1605));
        var0.put(Character.valueOf('n'), Integer.valueOf(1606));
        var0.put(Character.valueOf('h'), Integer.valueOf(1607));
        var0.put(Character.valueOf('w'), Integer.valueOf(1608));
        var0.put(Character.valueOf('Y'), Integer.valueOf(1609));
        var0.put(Character.valueOf('y'), Integer.valueOf(1610));
        var0.put(Character.valueOf('F'), Integer.valueOf(1611));
        var0.put(Character.valueOf('N'), Integer.valueOf(1612));
        var0.put(Character.valueOf('K'), Integer.valueOf(1613));
        var0.put(Character.valueOf('a'), Integer.valueOf(1614));
        var0.put(Character.valueOf('u'), Integer.valueOf(1615));
        var0.put(Character.valueOf('i'), Integer.valueOf(1616));
        var0.put(Character.valueOf('~'), Integer.valueOf(1617));
        var0.put(Character.valueOf('o'), Integer.valueOf(1618));
        var0.put(Character.valueOf('`'), Integer.valueOf(1648));
        var0.put(Character.valueOf('{'), Integer.valueOf(1649));
        var0.put(Character.valueOf('P'), Integer.valueOf(1662));
        var0.put(Character.valueOf('J'), Integer.valueOf(1670));
        var0.put(Character.valueOf('V'), Integer.valueOf(1700));
        var0.put(Character.valueOf('G'), Integer.valueOf(1711));
        s = Collections.unmodifiableMap(var0);
        (var0 = new HashMap()).put(Character.valueOf('C'), Integer.valueOf(1569));
        var0.put(Character.valueOf('M'), Integer.valueOf(1570));
        var0.put(Character.valueOf('O'), Integer.valueOf(1571));
        var0.put(Character.valueOf('W'), Integer.valueOf(1572));
        var0.put(Character.valueOf('I'), Integer.valueOf(1573));
        var0.put(Character.valueOf('Q'), Integer.valueOf(1574));
        var0.put(Character.valueOf('A'), Integer.valueOf(1575));
        var0.put(Character.valueOf('b'), Integer.valueOf(1576));
        var0.put(Character.valueOf('p'), Integer.valueOf(1577));
        var0.put(Character.valueOf('t'), Integer.valueOf(1578));
        var0.put(Character.valueOf('v'), Integer.valueOf(1579));
        var0.put(Character.valueOf('j'), Integer.valueOf(1580));
        var0.put(Character.valueOf('H'), Integer.valueOf(1581));
        var0.put(Character.valueOf('x'), Integer.valueOf(1582));
        var0.put(Character.valueOf('d'), Integer.valueOf(1583));
        var0.put(Character.valueOf('V'), Integer.valueOf(1584));
        var0.put(Character.valueOf('r'), Integer.valueOf(1585));
        var0.put(Character.valueOf('z'), Integer.valueOf(1586));
        var0.put(Character.valueOf('s'), Integer.valueOf(1587));
        var0.put(Character.valueOf('c'), Integer.valueOf(1588));
        var0.put(Character.valueOf('S'), Integer.valueOf(1589));
        var0.put(Character.valueOf('D'), Integer.valueOf(1590));
        var0.put(Character.valueOf('T'), Integer.valueOf(1591));
        var0.put(Character.valueOf('Z'), Integer.valueOf(1592));
        var0.put(Character.valueOf('E'), Integer.valueOf(1593));
        var0.put(Character.valueOf('g'), Integer.valueOf(1594));
        var0.put(Character.valueOf('_'), Integer.valueOf(1600));
        var0.put(Character.valueOf('f'), Integer.valueOf(1601));
        var0.put(Character.valueOf('q'), Integer.valueOf(1602));
        var0.put(Character.valueOf('k'), Integer.valueOf(1603));
        var0.put(Character.valueOf('l'), Integer.valueOf(1604));
        var0.put(Character.valueOf('m'), Integer.valueOf(1605));
        var0.put(Character.valueOf('n'), Integer.valueOf(1606));
        var0.put(Character.valueOf('h'), Integer.valueOf(1607));
        var0.put(Character.valueOf('w'), Integer.valueOf(1608));
        var0.put(Character.valueOf('Y'), Integer.valueOf(1609));
        var0.put(Character.valueOf('y'), Integer.valueOf(1610));
        var0.put(Character.valueOf('F'), Integer.valueOf(1611));
        var0.put(Character.valueOf('N'), Integer.valueOf(1612));
        var0.put(Character.valueOf('K'), Integer.valueOf(1613));
        var0.put(Character.valueOf('a'), Integer.valueOf(1614));
        var0.put(Character.valueOf('u'), Integer.valueOf(1615));
        var0.put(Character.valueOf('i'), Integer.valueOf(1616));
        var0.put(Character.valueOf('X'), Integer.valueOf(1617));
        var0.put(Character.valueOf('o'), Integer.valueOf(1618));
        var0.put(Character.valueOf('e'), Integer.valueOf(1648));
        var0.put(Character.valueOf('L'), Integer.valueOf(1649));
        var0.put(Character.valueOf('P'), Integer.valueOf(1662));
        var0.put(Character.valueOf('J'), Integer.valueOf(1670));
        var0.put(Character.valueOf('B'), Integer.valueOf(1700));
        var0.put(Character.valueOf('G'), Integer.valueOf(1711));
        t = Collections.unmodifiableMap(var0);
        (var0 = new HashMap()).put(Character.valueOf('\''), Character.valueOf('C'));
        var0.put(Character.valueOf('|'), Character.valueOf('M'));
        var0.put(Character.valueOf('}'), Character.valueOf('Q'));
        var0.put(Character.valueOf('*'), Character.valueOf('V'));
        var0.put(Character.valueOf('$'), Character.valueOf('c'));
        var0.put(Character.valueOf('{'), Character.valueOf('L'));
        var0.put(Character.valueOf('`'), Character.valueOf('e'));
        var0.put(Character.valueOf('~'), Character.valueOf('X'));
        var0.put(Character.valueOf('>'), Character.valueOf('O'));
        var0.put(Character.valueOf('&'), Character.valueOf('W'));
        var0.put(Character.valueOf('<'), Character.valueOf('I'));
        u = Collections.unmodifiableMap(var0);
        (var0 = new HashMap()).put(Character.valueOf('C'), Character.valueOf('\''));
        var0.put(Character.valueOf('M'), Character.valueOf('|'));
        var0.put(Character.valueOf('Q'), Character.valueOf('}'));
        var0.put(Character.valueOf('V'), Character.valueOf('*'));
        var0.put(Character.valueOf('c'), Character.valueOf('$'));
        var0.put(Character.valueOf('L'), Character.valueOf('{'));
        var0.put(Character.valueOf('e'), Character.valueOf('`'));
        var0.put(Character.valueOf('X'), Character.valueOf('~'));
        var0.put(Character.valueOf('O'), Character.valueOf('>'));
        var0.put(Character.valueOf('W'), Character.valueOf('&'));
        var0.put(Character.valueOf('I'), Character.valueOf('<'));
        v = Collections.unmodifiableMap(var0);
        w = a();
        (var0 = new HashMap()).put(",", String.valueOf(Character.toChars(Integer.valueOf("060c", 16).intValue())));
        var0.put("?", String.valueOf(Character.toChars(Integer.valueOf("061f", 16).intValue())));
        var0.put(";", String.valueOf(Character.toChars(Integer.valueOf("061b", 16).intValue())));
        var0.put(",", String.valueOf(Character.toChars(Integer.valueOf("066B", 16).intValue())));
        x = var0;
        y = Integer.valueOf("0600", 16).intValue();
        z = Integer.valueOf("06ff", 16).intValue();
    }

}
