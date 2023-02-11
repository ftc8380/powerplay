package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
@Config
public class V4BFull extends LinearOpMode {
    public static double LIFTROTATIONS = 2;

    public void runOpMode() {
        Sliders v4b = new Sliders(hardwareMap);

        waitForStart();
        v4b.floor();

        while(opModeIsActive()) {

            telemetry.addData("LL", v4b.motorLiftLeft.getCurrentPosition());
            telemetry.addData("LR", v4b.motorLiftRight.getCurrentPosition());
            telemetry.update();

            // Lift control
            if(gamepad1.dpad_up) {
                v4b.highJunction();

            } else if(gamepad1.dpad_down) {
                v4b.floor();
            } else if(gamepad1.dpad_right) {
                v4b.midJunction();
            }

            // Intake control
            if(gamepad1.right_trigger > 0.5) {
                v4b.closeClaw();
            } else if(gamepad1.left_trigger > 0.5) {
                v4b.openClaw();
            }
        }
    }
}
