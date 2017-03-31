package GoldDataGenerationUsingBabelNet;

import it.uniroma1.lcl.babelnet.*;
import it.uniroma1.lcl.babelnet.data.BabelPOS;
import it.uniroma1.lcl.babelnet.iterators.BabelSynsetIterator;
import it.uniroma1.lcl.babelnet.resources.ResourceID;
import it.uniroma1.lcl.jlt.util.Language;
import utilities.Preprocessing;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Maryam Aminian on 4/29/16.
 */
public class ExtractBabelNetTriplets2 {

    public static void main(String[] args) throws IOException {

        String tripletFilePath = args[0];
        //getBabelNetDics(tripletFilePath);
        renderBabelNetTriplets_as_Tharwa(tripletFilePath);
    }

    public static void getBabelNetDics(String tripletFilePath) throws IOException {
        BufferedWriter BabelNetTripletsWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tripletFilePath)));

        BabelNet bn = BabelNet.getInstance();
        BabelSynsetIterator bsi = bn.getSynsetIterator();
        int total_synset_counter = 0;
        int qualified_synste_counter = 0;

        while (bsi.hasNext()) {
            if (total_synset_counter % 10000 == 0)
                System.out.println(total_synset_counter);

            BabelSynset synset = bsi.next();
            total_synset_counter++;
            if (synset.getSynsetType().equals(BabelSynsetType.CONCEPT)) {

                String synset_id = synset.getId().toString();

                List<BabelSense> MSA_senses = synset.getSenses(Language.AR);
                List<BabelSense> En_senses = synset.getSenses(Language.EN);
                List<BabelSense> EGY_senses = synset.getSenses(Language.ARZ);

                if (MSA_senses.size() > 0 && En_senses.size() > 0 && EGY_senses.size() > 0) {

                    HashSet<String> MSA_senses_lemma = utils.getArabicSensesLemma(MSA_senses);
                    HashSet<String> EGY_senses_lemma = utils.getArabicSensesLemma(EGY_senses);
                    HashSet<String> En_senses_lemma = utils.getEnSensesLemma(En_senses);

                    if (MSA_senses_lemma.size() > 0 && EGY_senses_lemma.size() > 0 && En_senses_lemma.size() > 0) {
                        qualified_synste_counter++;
                        for (String En_sense : En_senses_lemma) {
                            for (String MSA_sense : MSA_senses_lemma) {
                                for (String EGY_sense : EGY_senses_lemma) {
                                    //found a qualified BabelNet triplet
                                    BabelNetTripletsWriter.write(synset_id + "\t" + En_sense + "\t" + MSA_sense + "\t" + EGY_sense + "\n");
                                }
                            }
                        }
                    }
                }

            }
        }

        System.out.println("Number of Extracted Synsets: " + qualified_synste_counter);
        BabelNetTripletsWriter.flush();
        BabelNetTripletsWriter.close();
    }


    public static void renderBabelNetTriplets_as_Tharwa(String filePath) throws IOException {
        //file path here indicates to the list of BabelNet triplets found in Parallel Vocabulary
        System.out.println("Loading BabelNet Triplets from BabelNet_triplets_common_wp file...");
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        BufferedWriter writer = new BufferedWriter(new FileWriter("BabelNet_triplets_common_wp_in_Tharwa_format"));
        writer.write("ID\tEGY_Source\tCODA\tMSA_Equivalent\tMSA_Lemma\tEGY_POS_CHE\tEGY_Word\tEnglish_Equivalent" +
                "\tEGY_POS_LDC\tMSA_POS_CHE\tMSA_POS_LDC\tEGY_Lemma\tEGY_Gender\tEGY_Number\tEGY_Rationality" +
                "\tCODA_Morph_Pattern\tMSA_Lemma_Morph_Pattern\tCODA_Root\tMSA_Lemma_Root\tEGY_Verb_Voice\n");

        String line2read = "";
        int counter = 0;

        while ((line2read = reader.readLine()) != null) {
            counter++;
            if (counter % 1000 == 0)
                System.out.println(counter);

            line2read = line2read.trim();
            String[] splitLine = line2read.split("\t");
            String En = splitLine[0];
            String En_POS = getPOS(splitLine[1].charAt(0));
            String MSA = splitLine[2];
            String MSA_POS = getPOS(splitLine[3].charAt(0));
            String EGY = splitLine[4];
            String EGY_POS = getPOS(splitLine[5].charAt(0));

            writer.write(counter + "\t-\t" + EGY + "\t" + MSA + "\t" + MSA + "\t-\t" + EGY + "\t" + En + "\t" + EGY_POS +
                    "\t-\t" + MSA_POS + "\t" + EGY + "\t-\t-\t-\t-\t-\t-\t-\t-\n");
        }

        writer.flush();
        writer.close();
    }

    public static String getPOS(char tag) {
        switch (tag) {
            case 'a':
                return "ADJECTIVE";
            case 'b':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'q':
            case 's':
            case 'u':
            default:
                return null;
            case 'c':
                return "CONJUNCTION";
            case 'd':
                return "DETERMINER";
            case 'i':
                return "INTERJECTION";
            case 'n':
                return "NOUN";
            case 'o':
                return "PRONOUN";
            case 'p':
                return "PREPOSITION";
            case 'r':
                return "ADVERB";
            case 't':
                return "ARTICLE";
            case 'v':
                return "VERB";
        }

    }
}
