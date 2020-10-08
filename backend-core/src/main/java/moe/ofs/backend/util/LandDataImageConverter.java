package moe.ofs.backend.util;

import moe.ofs.backend.services.map.ParkingInfoMapService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LandDataImageConverter {
    public static void main(String[] args) throws IOException, URISyntaxException {

        long[] mapGs = {-0, -455000};
        long[] mapGd = {-600000, 400000};

        long mapWidth = mapGd[1] - mapGs[1];
        long mapHeight = mapGd[0] - mapGs[0];

        final int scale = 20;
        final int tileSize = 256;

        BufferedImage bufferedImage = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);

        int spanHorizontal = 167;
        int spanVertical = 117;

        System.out.println("spanHorizontal = " + spanHorizontal);
        System.out.println("spanVertical = " + spanVertical);

        String prefix = "/data/nevada/";
        URI uri = WeirdTestClass.class.getResource(prefix).toURI();
        Path destPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());  // <String, Object>
            destPath = fileSystem.getPath(prefix);
        } else {
            destPath = Paths.get(uri);
        }

        Files.walk(destPath, 1)
                .filter(Files::isRegularFile)
                .collect(Collectors.groupingBy(path -> path.getFileName().toString().split("_")[1]))
                .forEach((level, pathList) -> {
                    Object[][] imagePack = new Object[spanHorizontal][spanVertical];

                    pathList.parallelStream().forEach(path -> {
                        String fileName = path.getFileName().toString();
                        try (InputStream inputStream = ParkingInfoMapService.class
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

                            String[] meta = fileName.split("_");

                            int x = Integer.parseInt(meta[2]);
                            int y = Integer.parseInt(meta[3]);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(bufferedImage, "png", baos);
                            baos.flush();
                            byte[] imageInByte = baos.toByteArray();
                            baos.close();

                            imagePack[x][y] = imageInByte;

                            System.out.println(String.format("parsing %d, %d", x, y));
//
//                File outputFile = new File("backend-core/src/main/resources/data/atlas/nevada/" +
//                        fileName + ".png");
//                System.out.println(outputFile);
//                ImageIO.write(bufferedImage, "png", outputFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    try(FileOutputStream fileOutputStream = new FileOutputStream("atlas_" + level);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                        objectOutputStream.writeObject(imagePack);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }
}

class WeirdTestClass {
    public static void main(String[] args) throws URISyntaxException, IOException {
        String prefix = "/data/nevada/";
        URI uri = WeirdTestClass.class.getResource(prefix).toURI();
        Path path;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());  // <String, Object>
            path = fileSystem.getPath(prefix);
        } else {
            path = Paths.get(uri);
        }
        Stream<Path> walk = Files.walk(path, 1);
        for (Iterator<Path> it = walk.iterator(); it.hasNext();){
            System.out.println(it.next());
        }
    }
}
