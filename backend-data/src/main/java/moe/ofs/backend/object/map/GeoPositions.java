package moe.ofs.backend.object.map;

import moe.ofs.backend.object.map.GeoPosition;
import moe.ofs.backend.object.map.Orientation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

/**
 * GeoPositions class is used to convert unit
 */
public class GeoPositions {
    public static String toLatLonDisplay(GeoPosition position) {
        Object[] displays = Arrays.stream(formatStringArray(position, false)).map(s -> (Object) s).toArray();
        return String.format("%s %s°%s'%s\" %s %s°%s'%s\"", displays);
    }

    public static String toLatLonAltDisplay(GeoPosition position) {
        Object[] displays = Arrays.stream(formatStringArray(position, false)).map(s -> (Object) s).toArray();
        return String.format("%s %s°%s'%s\" %s %s°%s'%s\", %s", displays);
    }

    public static String[] formatStringArray(GeoPosition position, boolean precision) {

        BigDecimal angleMultiplier = BigDecimal.valueOf(60);

        BigDecimal latDeg = BigDecimal.valueOf(Math.abs(position.getLatitude()));
        BigDecimal latMin = latDeg.subtract(BigDecimal.valueOf(latDeg.intValue())).multiply(angleMultiplier);
        BigDecimal latSec = latMin.subtract(BigDecimal.valueOf(latMin.intValue())).multiply(angleMultiplier);

        BigDecimal lonDeg = BigDecimal.valueOf(Math.abs(position.getLongitude()));
        BigDecimal lonMin = lonDeg.subtract(BigDecimal.valueOf(lonDeg.intValue())).multiply(angleMultiplier);
        BigDecimal lonSec = lonMin.subtract(BigDecimal.valueOf(lonMin.intValue())).multiply(angleMultiplier);


        Orientation latOrient = position.getLatitude() >= 0 ? Orientation.NORTH : Orientation.SOUTH;
        Orientation lonOrient = position.getLongitude() >= 0 ? Orientation.EAST : Orientation.WEST;

        int latDegDisplay = latDeg.intValue();
        int latMinDisplay = latMin.intValue();
        int latSecDisplay = latSec.round(new MathContext(2)).intValue();

        int lonDegDisplay = lonDeg.intValue();
        int lonMinDisplay = lonMin.intValue();
        int lonSecDisplay = lonSec.round(new MathContext(2)).intValue();

        if(precision) {
            return new String[] {
                    latOrient.getSymbol(),
                    String.valueOf(latDegDisplay),
                    String.valueOf(latMinDisplay),

                    lonOrient.getSymbol(),
                    String.valueOf(lonDegDisplay),
                    String.valueOf(lonMinDisplay),

                    String.valueOf(position.getAltitude())
            };
        } else {
            return new String[] {
                    latOrient.getSymbol(),
                    String.valueOf(latDegDisplay),
                    String.valueOf(latMinDisplay),
                    String.valueOf(latSecDisplay),

                    lonOrient.getSymbol(),
                    String.valueOf(lonDegDisplay),
                    String.valueOf(lonMinDisplay),
                    String.valueOf(lonSecDisplay),

                    String.valueOf(position.getAltitude())
            };
        }
    }

    public static GeoPosition get(Orientation orientLat, String latDegree, String latMinute, String latSecond,
                                  Orientation orientLon, String lonDegree, String lonMinute, String lonSecond) {
        double latDegVal = Double.parseDouble(latDegree);
        double latMinVal = Double.parseDouble(latMinute);
        double latSecVal = Double.parseDouble(latSecond);

        double lonDegVal = Double.parseDouble(lonDegree);
        double lonMinVal = Double.parseDouble(lonMinute);
        double lonSecVal = Double.parseDouble(lonSecond);

        double totalLatDeg = latDegVal + latMinVal / 60 + latSecVal / 3600;
        double totalLonDeg = lonDegVal + lonMinVal / 60 + lonSecVal / 3600;

        return new GeoPosition(orientLat.getSign() * totalLatDeg,
                orientLon.getSign() * totalLonDeg);
    }

    public static GeoPosition getPrecision(String latDegree, String latMinute,
                                           String lonDegree, String lonMinute) {
        long latDegVal = Long.parseLong(latDegree);
        long latMinVal = Long.parseLong(latMinute);

        long lonDegVal = Long.parseLong(lonDegree);
        long lonMinVal = Long.parseLong(lonMinute);

        long totalLatDeg = latDegVal + latMinVal / 60;
        long totalLonDeg = lonDegVal + lonMinVal / 60;

        return new GeoPosition(totalLatDeg, totalLonDeg);
    }
}
