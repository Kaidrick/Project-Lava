package moe.ofs.backend.function.coordoffset;

import moe.ofs.backend.object.Vector3D;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class OffsetTest {

    double distance = 1;
    Vector3D base = new Vector3D(0, 0, 1);

    double bearing000 = 0;
    double bearing045 = Math.PI * 0.25;
    double bearing090 = Math.PI * 0.5;
    double bearing135 = Math.PI * 0.75;
    double bearing180 = Math.PI;

    double bearing225 = Math.PI * 1.25;
    double bearing270 = Math.PI * 1.5;
    double bearing315 = Math.PI * 1.75;
    double bearing360 = Math.PI * 2;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void of() {

//        Vector3D offset000 = Offset.of(distance, bearing000, base);
//        System.out.println("offset000 = " + offset000);
//
//        Vector3D offset045 = Offset.of(distance, bearing045, base);
//        System.out.println("offset045 = " + offset045);
//
//        Vector3D offset090 = Offset.of(distance, bearing090, base);
//        System.out.println("offset090 = " + offset090);
//
//        Vector3D offset135 = Offset.of(distance, bearing135, base);
//        System.out.println("offset135 = " + offset135);
//
//        Vector3D offset180 = Offset.of(distance, bearing180, base);
//        System.out.println("offset180 = " + offset180);
//
//        Vector3D offset225 = Offset.of(distance, bearing225, base);
//        System.out.println("offset225 = " + offset225);
//
//        Vector3D offset270 = Offset.of(distance, bearing270, base);
//        System.out.println("offset270 = " + offset270);
//
//        Vector3D offset315 = Offset.of(distance, bearing315, base);
//        System.out.println("offset315 = " + offset315);
//
//        Vector3D offset360 = Offset.of(distance, bearing360, base);
//        System.out.println("offset360 = " + offset360);
    }
}