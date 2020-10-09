package moe.ofs.backend.util;

import moe.ofs.backend.services.map.ParkingInfoMapService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class LandDataImageConverter {

    static long[] mapGs = {-0, -455000};
    static long[] mapGd = {-600000, 400000};

    static long mapWidth = mapGd[1] - mapGs[1];
    static long mapHeight = mapGd[0] - mapGs[0];

    static final int scale = 20;
    static final int tileSize = 256;

    private static String getTileIdentName(Path p) {
        String fileName = p.getFileName().toString();
        String[] meta = fileName.split("_");
        String theater = meta[0];
        int level = Integer.parseInt(meta[1]);
        int x = Integer.parseInt(meta[2]);
        int y = Integer.parseInt(meta[3]);

        return String.format("%s_%d_%d_%d", theater, level, x, y);
    }

    private static byte[] parseFromPath(Path p) {
        BufferedImage bufferedImage = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        String fileName = p.getFileName().toString();
        try (InputStream inputStream = LandDataImageConverter.class
                .getResourceAsStream("/data/nevada/" + fileName)) {
            for (int i = 0; i < tileSize; i++) {
                for (int j = 0; j < tileSize; j++) {

                    char c = (char) inputStream.read();

                    int alpha = 255; //don't forget this, or use BufferedImage.TYPE_INT_RGB instead
                    int red = 0;
                    int green = 0;
                    int blue = 0;

                    switch (c) {
                        case '_':  // land
                            red = 188;
                            green = 149;
                            blue = 84;
                            break;

                        case '=':
                            red = 55;
                            green = 130;
                            blue = 137;
                            break;

                        case '~':
                            red = 55;
                            green = 130;
                            blue = 167;
                            break;

                        case '#':  // road
                            red = 159;
                            green = 39;
                            blue = 22;
                            break;

                        case '%':  // runways
                            red = 148;
                            green = 145;
                            blue = 132;
                            break;

                        case '^':  // mountain peaks
                            red = 255;
                            green = 255;
                            blue = 255;
                            break;

                        case 'x':  // scenery
                            red = 90;
                            green = 88;
                            blue = 74;
                            break;

                        default:
                            try {  // number that represents altitude in kilometer
                                int height = Integer.parseInt(String.valueOf(c));
                                red = 188;
                                green = 149 + 6 * height;
                                blue = 84  + 6 * height;
                            } catch (Exception e) {
                                // ignore
                            }
                            break;
                    }

                    Color color = new Color(red, green, blue, alpha);
                    bufferedImage.setRGB(j, i, color.getRGB());
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            baos.flush();

            byte[] imageInByte = baos.toByteArray();

            baos.close();

            return imageInByte;

        } catch (IOException e) {
            e.printStackTrace();

            return new byte[] {};
        }
    }


    public static void main(String[] args) throws IOException, URISyntaxException {

        int spanHorizontal = 167;
        int spanVertical = 117;

        System.out.println("spanHorizontal = " + spanHorizontal);
        System.out.println("spanVertical = " + spanVertical);

        String prefix = "/data/nevada/";
        URI uri = ParkingInfoMapService.class.getResource(prefix).toURI();
        Path destPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());  // <String, Object>
            destPath = fileSystem.getPath(prefix);
        } else {
            destPath = Paths.get(uri);
        }

        // use a super sized map to contain these image byte array data
        Map<String, byte[]> map = Files.walk(destPath, 1)
                .filter(Files::isRegularFile)
                .parallel()
                .collect(Collectors.toMap(LandDataImageConverter::getTileIdentName, LandDataImageConverter::parseFromPath));


        try (FileOutputStream fileOutputStream = new FileOutputStream("atlas_20.ser");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(map);
        }
    }
}
