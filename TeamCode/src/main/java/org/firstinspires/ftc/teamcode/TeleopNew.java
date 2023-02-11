package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class TeleopNew extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        // For some reason FTC forces you to set target position before RUN_TO_POSITION encoder mode

        DcMotor motorFrontLeft = hardwareMap.dcMotor.get("motor front left");
        DcMotor motorBackLeft = hardwareMap.dcMotor.get("motor back left");
        DcMotor motorFrontRight = hardwareMap.dcMotor.get("motor front right");
        DcMotor motorBackRight = hardwareMap.dcMotor.get("motor back right");
        motorFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        double scale = 0.55; // 1.0 speed is a bit too fast

        // Used for single trigger pull detection
        Gamepad currentGamepad2 = new Gamepad();
        Gamepad previousGamepad2 = new Gamepad();
        Gamepad currentGamepad1 = new Gamepad();
        Gamepad previousGamepad1 = new Gamepad();

        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(new BNO055IMU.Parameters());

        Sliders v4b = new Sliders(hardwareMap);
        waitForStart();

        if (isStopRequested()) return;
        v4b.floor();

        while(opModeIsActive()) {
            // This makes toggle switches possible
            try {
                previousGamepad1.copy(currentGamepad1);
                previousGamepad2.copy(currentGamepad2);
                currentGamepad1.copy(gamepad1);
                currentGamepad2.copy(gamepad2);
            }
            catch (RobotCoreException e) {
                // Swallow any error
            }

            // Slow mode
            if(currentGamepad1.dpad_down && !previousGamepad1.dpad_down) {
                scale = 0.4;
            } else if(currentGamepad1.dpad_left && !previousGamepad1.dpad_left) {
                scale = 0.55;
            } else if(currentGamepad1.dpad_right && !previousGamepad1.dpad_right) {
                scale = 0.55;
            } else if(currentGamepad1.dpad_up && !previousGamepad1.dpad_up) {
                scale = 0.75;
            }

            double y = -gamepad1.left_stick_y; // Why is y still reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            double botHeading = -imu.getAngularOrientation().firstAngle;
            double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
            double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = scale * (rotY + rotX + rx) / denominator;
            double backLeftPower = scale * (rotY - rotX + rx) / denominator;
            double frontRightPower = scale * (rotY - rotX - rx) / denominator;
            double backRightPower = scale * (rotY + rotX - rx) / denominator;
            motorFrontLeft.setPower(frontLeftPower);
            motorBackLeft.setPower(backLeftPower);
            motorFrontRight.setPower(frontRightPower);
            motorBackRight.setPower(backRightPower);

            if(gamepad2.dpad_up) {
                v4b.highJunction();
            } else if(gamepad2.dpad_right) {
                v4b.midJunction();
            } else if(gamepad2.dpad_left) {
                    v4b.lowJunction();
            } else if(gamepad2.dpad_down) {
                v4b.floor();
            }

            if(gamepad2.right_trigger > 0.5) {
                v4b.closeClaw();
            } else if(gamepad2.left_trigger > 0.5) {
                v4b.openClaw();
            }

        }
    }

    // Used for lift, motor rotations --> encoder ticks
    private int rotationsToTicks(double rotations) {
        double ticksPerRotation = 384.5;
        return (int) (rotations * ticksPerRotation);
    }
}