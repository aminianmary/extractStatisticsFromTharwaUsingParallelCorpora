package GoldDataGenerationUsingBabelNet;

import edu.mit.jwi.item.POS;
import it.uniroma1.lcl.babelnet.*;
import it.uniroma1.lcl.babelnet.data.BabelPOS;
import it.uniroma1.lcl.babelnet.data.BabelSenseSource;
import it.uniroma1.lcl.babelnet.iterators.BabelSynsetIterator;
import it.uniroma1.lcl.jlt.util.*;
import utilities.Preprocessing;

import java.util.*;
import java.io.*;

/**
 * Created by Maryam Aminian on 2/10/16.
 */
public class GenerateGoldDataUsingBabelNet {

        public static void main(String[] args) throws IOException
        {
            String transDict= args[0];
            String transDict_MSA_En_found_in_BableNet= args[1];
            String BabelNet_synset_found_in_transDict= args[2];
            String log= args[3];

            Object[] obj= createTransDict_Dic(transDict);
            HashMap<String, HashSet<String>> transDict_En_POS_MSA= (HashMap<String, HashSet<String>>) obj[0];
            HashMap<String, HashSet<String>> transDict_En_MSA= (HashMap<String, HashSet<String>>) obj[1];


            BufferedWriter logWriter= new BufferedWriter(new FileWriter(log));
            logWriter.write("TransDict_En_POS size: "+ transDict_En_POS_MSA.size());
            logWriter.write("TransDict_En size: " + transDict_En_MSA.size());

            findTransDicEntriesInBabelNet(transDict_En_POS_MSA, transDict_MSA_En_found_in_BableNet, transDict_MSA_En_found_in_BableNet+"_not_found");
            logWriter.write("Done with finding TransDict entries in BabelNet....\n");

            findBabelNetEntriesInTransDict(transDict_En_MSA, BabelNet_synset_found_in_transDict, BabelNet_synset_found_in_transDict + "_not_found");
            logWriter.write("Done with finding BabelNet synsets in TransDict....\n");

            logWriter.flush();
            logWriter.close();

        }


    public static void findBabelNetEntriesInTransDict (HashMap<String, HashSet<String>> transDict_En_MSA,
                                                       String found_BabelNet_Synsets,
                                                       String not_found_BabelNet_Synsets) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(found_BabelNet_Synsets), "utf-8"));
        BufferedWriter not_found_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(not_found_BabelNet_Synsets), "utf-8"));


        BabelNet bn = BabelNet.getInstance();
        BabelSynsetIterator bsi = bn.getSynsetIterator();
        int synset_counter = 0;

        while (bsi.hasNext()) {
            if (synset_counter % 1000000 == 0)
                System.out.println(synset_counter);

            BabelSynset synset = bsi.next();
            if (synset.getSynsetType().equals(BabelSynsetType.CONCEPT) &&
                    (synset.getSynsetSource().equals(BabelSynsetSource.WN) || synset.getSynsetSource().equals(BabelSynsetSource.WIKIWN))) {

                String synset_id = synset.getId().toString();
                synset_counter++;

                List<BabelSense> Ar_senses = synset.getSenses(Language.AR);
                List<BabelSense> En_senses = synset.getSenses(Language.EN);

                if (Ar_senses.size() >0 && En_senses.size()>0)
                {

                HashSet<String> AR_top_senses = utils.getArabicSensesLemma(Ar_senses);
                HashSet<String> En_top_senses = utils.getEnSensesLemma(En_senses);

                //check if TransDict contains senses from This synset

                    for (String BableNet_En : En_top_senses) {
                        if (transDict_En_MSA.containsKey(BableNet_En)) {
                            HashSet<String> common_MSA_senses = new HashSet<String>(transDict_En_MSA.get(BableNet_En));
                            common_MSA_senses.retainAll(AR_top_senses);
                            if (common_MSA_senses.size() > 0) {
                                //found a BabelSynset with at least one common MSA-En sense
                                writer.write(synset_id + "\tCOM_EN:" + BableNet_En + "\tCOM_MSA:" + common_MSA_senses.toString() +
                                        "\tBableEn:" + En_top_senses.toString() + "\tBableMSA:" + AR_top_senses + "\n");

                                break;
                            }else
                                not_found_writer.write(synset_id+"\n");
                        }else
                            not_found_writer.write(synset_id+"\n");
                    }
                }

            }
        }

        writer.flush();
        writer.close();
        not_found_writer.flush();
        not_found_writer.close();
    }

    public static void findTransDicEntriesInBabelNet (HashMap<String, HashSet<String>> transDict_En_POS_MSA,
                                                      String found_MSA_En_list,
                                                      String Not_found_MSA_En_list) throws IOException
    {

        BufferedWriter writer= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(found_MSA_En_list),"utf-8"));
        BufferedWriter not_found_writer= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Not_found_MSA_En_list),"utf-8"));

        BabelNet bn = BabelNet.getInstance();
        System.out.println("Number of EN-POS in TransDict: " + transDict_En_POS_MSA.size());

        int counter=0;
        for (String En_POS: transDict_En_POS_MSA.keySet())
        {
            counter++;

            if (counter%1000000==0)
                System.out.println(counter);

            String En= En_POS.split("@@pos@@")[0];
            String pos = En_POS.split("@@pos@@")[1];
            HashSet<String> MSA_equivalents= transDict_En_POS_MSA.get(En_POS);
            BabelPOS BabelPOS= utils.returnBabelNetPOS(pos);

            List<BabelSynset> synsets= bn.getSynsets(Language.EN, En, BabelPOS);
            HashSet<String> MSA_senses= new HashSet<String>();
            for (BabelSynset synset: synsets) {
                List<BabelSense> senses= synset.getSenses(Language.AR);
                MSA_senses.addAll(utils.getArabicSensesLemma(senses));
            }

            //check if transDict MSA is found in the list of MSA senses
            for (String MSA: MSA_equivalents)
            {
                if (MSA_senses.contains(MSA))
                    writer.write(En+"\t"+pos+"\t"+MSA+"\n");
                else
                    not_found_writer.write(En+"\t"+pos+"\t"+MSA+"\n");
            }
        }

        writer.flush();
        writer.close();
        not_found_writer.flush();
        not_found_writer.close();

    }

    public static Object[] createTransDict_Dic(String filePath) throws IOException
    {
        BufferedReader reader= new BufferedReader(new FileReader(filePath));
        HashMap<String, HashSet<String>> transDict_En_POS= new HashMap<String, HashSet<String>>();
        HashMap<String, HashSet<String>> transDict_En= new HashMap<String, HashSet<String>>();

        String line2read="";
        int counter=0;
        while ((line2read= reader.readLine())!= null)
        {
            counter++;

            if (counter%100000==0)
                System.out.println(counter);

            String[] splitLine= line2read.trim().split("\t");
            final String MSA= Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(splitLine[3]));
            String POS = splitLine[1];
            String En= splitLine[2];

            if (POS.equals(""))
                POS="null";
            //creating transDict_En_POS
            if (!transDict_En_POS.containsKey(En+"@@pos@@"+POS)){
                transDict_En_POS.put(En + "@@pos@@" + POS, new HashSet<String>() {{
                    add(MSA);
                }});
            }
            else {
                transDict_En_POS.get(En + "@@pos@@" + POS).add(MSA);
            }

            //creating transDict_En
            if (!transDict_En.containsKey(En)){
                transDict_En.put(En, new HashSet<String>() {{
                    add(MSA);
                }});
            }
            else {
                transDict_En.get(En).add(MSA);
            }
        }
        return new Object[] {transDict_En_POS, transDict_En};
    }



}

