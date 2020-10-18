import java.util.*;

public class HashListAutocomplete implements Autocompletor {
    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize = 0;

    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
            throw new NullPointerException("One or more arguments null");
        }

        if (terms.length != weights.length) {
            throw new IllegalArgumentException("terms and weights are not the same length");
        }

        initialize(terms,weights);
    }


    @Override
    public List<Term> topMatches(String prefix, int k) {
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
            if(myMap.containsKey(terms[i].substring(MAX_PREFIX))==false) {
                myMap.put(terms[i].substring(MAX_PREFIX), new ArrayList<Term>());
            }
            myMap.get(terms[i].substring(MAX_PREFIX)).add(new Term(terms[i], weights[i]));
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
