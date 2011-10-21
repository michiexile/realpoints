import edu.stanford.math.plex.*;
import java.io.*;
import java.util.*;


class RipsHomology {
    public static void main(String[] args) throws Exception {
        if(args.length != 4) {
            System.out.println("Usage: RipsHomology data-file output-slug maxdim epsilon\n");
            System.exit(-1);
        }

        String filename, slug;
        Integer maxdim;
        Double epsilon;

        filename = args[0];
        slug = args[1];
        maxdim = Integer.parseInt(args[2]);
        epsilon = Double.parseDouble(args[3]);

        // Read in dataset
        File datafile = null;
        BufferedReader br = null;
        String line = null;
        StringTokenizer st = null;
        ArrayList<ArrayList<Double>> al = new ArrayList<ArrayList<Double>>();
        int nL=0, nT=0;

        datafile = new File(filename);
        br = new BufferedReader(new FileReader(datafile));

        while( (line=br.readLine()) != null) {
            nT = 0;
            ArrayList<Double> entry = new ArrayList<Double>();
            st = new StringTokenizer(line, ",");
            while (st.hasMoreTokens()) {
                entry.add(Double.parseDouble(st.nextToken()));
                nT++;
            }
            al.add(entry);
            nL++;
        }

        double[][] data = new double[nL][nT];
        int i=0;
        for(ArrayList<Double> el : al) {
            int j=0;
            for(Double entry : el) {
                data[i][j] = entry.doubleValue();
                j++;
            }
            i++;
        }

        // Construct EuclideanArrayData
        EuclideanArrayData ead = new EuclideanArrayData(data);
        System.out.print("Point count: ");
        System.out.println(ead.count());

        // Construct WitnessComplex
        RipsStream rips = Plex.RipsStream(0.0001, maxdim, epsilon, ead);

        System.out.print("Rips complex of size: ");
        System.out.println(rips.size());

        // Compute persistent homology
        PersistenceInterval.Float[] intervals = Plex.Persistence().computeIntervals(rips,false,13);

        // Write out everything
        for(i=0; i<maxdim; i++) {
            FileWriter output = new FileWriter(slug + "-" + Integer.toString(i) + ".dgm");
            BufferedWriter out = new BufferedWriter(output);
            
            for(PersistenceInterval.Float pif : intervals) {
                if(pif.dimension == i) {
                    out.write(Double.toString(pif.start));
                    out.write(",");
                    out.write(Double.toString(pif.end));
                    out.newLine();
                }
            }
            out.close();
        }
    }
}
