package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Config
@TeleOp
public class V4BTuner extends LinearOpMode {
    public static double LIFTROTATIONS = 0.0;
    public static double V4BROTATIONS = 0.0;

    public void runOpMode() {
        Sliders v4b = new Sliders(hardwareMap);
        waitForStart();
        while(opModeIsActive()) {

            if(gamepad1.a) {
                v4b.v4b(V4BROTATIONS);
                v4b.motorGrouping(LIFTROTATIONS);
            }

            if(gamepad1.right_trigger > 0.5) {
                v4b.closeClaw();
            } else if(gamepad1.left_trigger > 0.5) {
                v4b.openClaw();
            }
        }
    }
}
