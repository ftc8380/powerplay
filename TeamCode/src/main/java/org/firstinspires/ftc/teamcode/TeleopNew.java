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

        DcMotor motorLift = hardwareMap.dcMotor.get("motor lift");
        motorLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLift.setTargetPosition(0);
        motorLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

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

        Servo servoIntake = hardwareMap.servo.get("servo intake");
        waitForStart();

        if (isStopRequested()) return;

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

            // Lift logic
            motorLift.setPower(0.6);
            if (gamepad2.dpad_up) { // Max height
                motorLift.setTargetPosition(rotationsToTicks(6.9));
            } else if (gamepad2.dpad_down) { // Fully retracted
                motorLift.setTargetPosition(0);
            } else if(gamepad2.dpad_left) {
                motorLift.setTargetPosition(rotationsToTicks(0.5));
            } else if(gamepad2.dpad_right) {
                motorLift.setTargetPosition(rotationsToTicks(4.9));
            }
            // Righty tighty lefty loosy
            if(gamepad2.right_trigger > 0.5) {
                servoIntake.setPosition(0.25);
            } else if(gamepad2.left_trigger > 0.5) {
                servoIntake.setPosition(0);
            }

        }
    }

    // Used for lift, motor rotations --> encoder ticks
    private int rotationsToTicks(double rotations) {
        double ticksPerRotation = 384.5;
        return (int) (rotations * ticksPerRotation);
    }
}