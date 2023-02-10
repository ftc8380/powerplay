package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
@Config
public class V4BFull extends LinearOpMode {
    public static double LIFTROTATIONS = 2;

    public void runOpMode() {
        Sliders lift = new Sliders(hardwareMap.dcMotor.get("motor lift left"),
                hardwareMap.dcMotor.get("motor lift right"),
                hardwareMap.servo.get("servo left v4b"),
                hardwareMap.servo.get("servo right v4b"));

        Servo servoIntake = hardwareMap.servo.get("servo intake");

        waitForStart();
        lift.floor();

        while(opModeIsActive()) {

            telemetry.addData("LL", lift.motorLiftLeft.getCurrentPosition());
            telemetry.addData("LR", lift.motorLiftRight.getCurrentPosition());
            telemetry.update();

            // Lift control
            if(gamepad1.dpad_up) {
                lift.highJunction();

            } else if(gamepad1.dpad_down) {
                servoIntake.setPosition(0.5);
                lift.floor();
            }

            // Intake control
            if(gamepad1.right_trigger > 0.5) {
                servoIntake.setPosition(0.5);
            } else if(gamepad1.left_trigger > 0.5) {
                servoIntake.setPosition(0.9);
            }
        }
    }
}
