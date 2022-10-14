package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class TurntableTest extends LinearOpMode {

    private final double TICKS_PER_REV = 1120.0;

    private final double TICKS_PER_DEGREE = TICKS_PER_REV / 360;
    private final double MOTOR_POWER = 1.0;
    private DcMotor mainMotor;

    public void runOpMode() {
        setupMotor();
        waitForStart();

        while(opModeIsActive()) {
            if(gamepad1.b)
                goToDegrees(0);

            else if(gamepad1.dpad_left)
                goToDegrees(90);

            else if(gamepad1.dpad_up)
                goToDegrees(180);

            else if(gamepad1.dpad_right)
                goToDegrees(270);

            else if(gamepad1.dpad_down)
                goToDegrees(360);
        }
    }

    void setupMotor() {
        mainMotor = hardwareMap.dcMotor.get("motor lift");
        mainMotor.setTargetPosition(0);
        mainMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mainMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    void setTargetDegrees(double degrees) {
        int ticksNeeded = (int) (degrees * TICKS_PER_DEGREE);
        mainMotor.setTargetPosition(ticksNeeded);
    }

    double getTargetDegrees() {
        return mainMotor.getCurrentPosition() / TICKS_PER_DEGREE;
    }

    void goToDegrees(double degrees) {
        mainMotor.setPower(MOTOR_POWER);
        setTargetDegrees(degrees);
    }
}
