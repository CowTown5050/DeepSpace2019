package frc.inputs;

import edu.wpi.first.wpilibj.Joystick;
import frc.utils.Constants;
import frc.utils.DriveHelper;
import frc.utils.DriveSignal;

/**
 * An implementation of the Driver's controls when the robot is being driven
 * with a joystick.
 */
public class DriverJoystick implements DriverHid {

    private static DriverJoystick instance;
    private final Joystick driverJoystick;

    private DriverJoystick() {
        driverJoystick = new Joystick(Constants.DRIVER_JOYSTICK_PORT);
    }

    public static DriverJoystick getInstance() {
        if (instance == null) {
            instance = new DriverJoystick();
        }
        return instance;
    }

    @Override
    public void update() {

    }

    @Override
    public void disabled() {

    }

    @Override
    public void disabledPeriodic() {

    }

    @Override
    public DriveSignal getDriveSignal() {
        return DriveHelper.arcadeToDriveSignal(-driverJoystick.getRawAxis(1), driverJoystick.getRawAxis(0));
    }

    @Override
    public boolean liftAllJacks() {
        return driverJoystick.getRawButton(1);
    }

    @Override
    public boolean retractAllJacks() {
        return driverJoystick.getRawButton(4);
    }

    @Override
    public boolean initializeHabClimbing() {
        return driverJoystick.getRawButton(8);
    }

    @Override
    public boolean manualJackWheelOverride() {
        return driverJoystick.getRawButton(9);
    }

    @Override
    public boolean zeroJacks() {
        return driverJoystick.getRawButton(10);
    }

    @Override
    public DriveSignal runJackWheels() {
        return DriveSignal.NEUTRAL;
//        double speed = driverJoystick.getRawAxis(3);
//        return new DriveSignal(speed, speed);
    }

    @Override
    public boolean cargoOuttakeRight() {
        return driverJoystick.getRawButton(6);
    }

    @Override
    public boolean cargoOuttakeLeft() {
        return driverJoystick.getRawButton(5);
    }

    @Override
    public boolean cargoIntakeRight() {
        return false;
    }

    @Override
    public boolean cargoIntakeLeft() {
        return false;
    }
}
