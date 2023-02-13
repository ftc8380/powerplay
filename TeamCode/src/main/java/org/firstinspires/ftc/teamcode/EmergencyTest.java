package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@Config
@TeleOp
public class EmergencyTest extends LinearOpMode {
    public static int POS = 2500;
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor motorV4b = hardwareMap.dcMotor.get("motor lift");
        waitForStart();
        while(opModeIsActive()) {
            if(gamepad1.dpad_up) {
                motorV4b.setPower(0.3);
            } else if(gamepad1.dpad_down) {
                motorV4b.setPower(-0.3);
            } else {
                motorV4b.setPower(0.0);
            }
        }
    }
}
