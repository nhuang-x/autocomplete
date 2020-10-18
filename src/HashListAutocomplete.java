import java.util.*;

public class HashListAutocomplete implements Autocompletor {
    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize = 0;

    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
            throw new NullPointerException("One or more arguments null");
        }

        initialize(terms,weights);
    }


    @Override
    public List<Term> topMatches(String prefix, int k) {
        if(myMap.containsKey(prefix) == false) {
            return new ArrayList<Term>();
        }
        if(k == 0) {
            return new ArrayList<Term>();
        }
        if(prefix.length() > MAX_PREFIX) {
            prefix = prefix.substring(0, MAX_PREFIX);
        }
        List<Term> all = myMap.get(prefix);
        return all.subList(0, Math.min(k, all.size()));
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        myMap = new HashMap<String, List<Term>>();
        for(int i = 0; i<terms.length;i++) {
            String prefix = terms[i];
            if(terms[i].length() > MAX_PREFIX) {
                prefix = prefix.substring(0, MAX_PREFIX);
            }
            if(myMap.containsKey(prefix)==false) {
                myMap.put(prefix, new ArrayList<Term>());
            }
            myMap.get(prefix).add(new Term(terms[i], weights[i]));
        }
        for(List<Term> list : myMap.values()) {
            Collections.sort(list,Comparator.comparing(Term::getWeight).reversed());
        }
    }

    @Override
    public int sizeInBytes() {
        if (mySize == 0) {
            for(String str : myMap.keySet()) {
                mySize += BYTES_PER_CHAR * str.length();
            }
            for(List<Term> list : myMap.values()) {
                for(Term term : list) {
                    mySize += BYTES_PER_DOUBLE +
                            BYTES_PER_CHAR*term.getWord().length();
                }
            }
        }
        return mySize;
    }
}
