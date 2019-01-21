package frc.subsystem;

import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import frc.utils.UnitConversions;

import static frc.utils.Constants.*;

public class Elevator extends Subsystem {
    private static final double FEEDFORWARD_NO_CARGO = 0.3;
    private static final double FEEDFORWARD_WITH_CARGO = 0.3;
    private static final MotorType ELEVATOR_MOTOR_TYPE = MotorType.kBrushless;
    // TODO measure
    private static final double LOWER_LIMIT_DISTANCE_FROM_GROUND = UnitConversions.inchesToMeters(6.0);
    // TODO measure
    private static final double TOP_LIFT_HEIGHT = UnitConversions.inchesToMeters(63);

    // todo tune and make static final
    private double KP = 0.1;
    private double KI = 0.1;
    private double KD = 0.1;
    private double KI_ZONE = 0.1;
    private double MAX_OUTPUT = 0.3;
    private double MIN_OUTPUT = -0.3;
    private double DESIRED_ROTATIONS = 0.1;

    private static Elevator instance;

    // TODO do we need to add right side to anything else after slaving?
    private final CANSparkMax left;
    private final CANSparkMax right;
    private final CANEncoder leftEncoder;
    private final CANPIDController leftController;

    // TODO add limit switch validation against cargo/hatches hitting it
    private final CANDigitalInput topLimit;
    private final CANDigitalInput bottomLimit;

    private PeriodicIO periodicIo = new PeriodicIO();
    private double offset = 0.0;

    private Elevator() {
        left = new CANSparkMax(LEFT_LIFT_NEO, ELEVATOR_MOTOR_TYPE);
        right = new CANSparkMax(RIGHT_LIFT_NEO, ELEVATOR_MOTOR_TYPE);
        leftEncoder = left.getEncoder();
        leftController = left.getPIDController();

        // TODO do we need to swap top or bottom?
        topLimit = left.getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyClosed);
        bottomLimit = left.getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyClosed);

        // Enabled by default, just leaving this here to make it quick to disable if needed
        topLimit.enableLimitSwitch(true);
        bottomLimit.enableLimitSwitch(true);
        // set PID coefficients
        leftController.setP(KP);
        leftController.setI(KI);
        leftController.setD(KD);
        leftController.setIZone(KI_ZONE);
        leftController.setFF(periodicIo.feedforward);
        leftController.setOutputRange(MIN_OUTPUT, MAX_OUTPUT);

        // display PID coefficients on ELEVATOR_SHUFFLEBOARD
        ELEVATOR_SHUFFLEBOARD.putNumber("P Gain", KP);
        ELEVATOR_SHUFFLEBOARD.putNumber("I Gain", KI);
        ELEVATOR_SHUFFLEBOARD.putNumber("D Gain", KD);
        ELEVATOR_SHUFFLEBOARD.putNumber("I Zone", KI_ZONE);
        ELEVATOR_SHUFFLEBOARD.putNumber("Feed Forward", periodicIo.feedforward);
        ELEVATOR_SHUFFLEBOARD.putNumber("Max Output", MAX_OUTPUT);
        ELEVATOR_SHUFFLEBOARD.putNumber("Min Output", MIN_OUTPUT);
        ELEVATOR_SHUFFLEBOARD.putNumber("Set Rotations", DESIRED_ROTATIONS);

        // TODO check inversion and make sure that following works
        right.setInverted(true);
        right.follow(left);
    }

    private static class PeriodicIO {
        // inputs
        double rawPosition;
        double velocity;
        double feedforward;
        // TODO implement me with query about having a ball
        boolean cargoHeld;

        // outputs
    }

    public static Elevator getInstance() {
        if (instance == null) {
            instance = new Elevator();
        }
        return instance;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        periodicIo.rawPosition = leftEncoder.getPosition();
        periodicIo.velocity = leftEncoder.getVelocity();
        // TODO reimplement this with a branch based on whether we have cargo or not
        //  periodicIo.feedforward = FEEDFORWARD_NO_CARGO;

        // TODO remove when making tuning values const
        double p = ELEVATOR_SHUFFLEBOARD.getNumber("P Gain", 0);
        double i = ELEVATOR_SHUFFLEBOARD.getNumber("I Gain", 0);
        double d = ELEVATOR_SHUFFLEBOARD.getNumber("D Gain", 0);
        double iZone = ELEVATOR_SHUFFLEBOARD.getNumber("I Zone", 0);
        double f = ELEVATOR_SHUFFLEBOARD.getNumber("Feed Forward", 0);

        if (f != periodicIo.feedforward) {
            periodicIo.feedforward = f;
        }
        double maxOutput = ELEVATOR_SHUFFLEBOARD.getNumber("Max Output", 0);
        double minOutput = ELEVATOR_SHUFFLEBOARD.getNumber("Min Output", 0);
        double desiredRotations = ELEVATOR_SHUFFLEBOARD.getNumber("Set Rotations", 0);

        if (p != KP) KP = p;
        if (i != KP) KI = i;
        if (d != KD) KD = d;
        if (iZone != KI_ZONE) KI_ZONE = iZone;
        if (maxOutput != MAX_OUTPUT || minOutput != MIN_OUTPUT) {
            MAX_OUTPUT = maxOutput;
            MIN_OUTPUT = minOutput;
            leftController.setOutputRange(MIN_OUTPUT, MIN_OUTPUT);
        }
        if (desiredRotations != DESIRED_ROTATIONS) DESIRED_ROTATIONS = desiredRotations;
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        leftController.setReference(DESIRED_ROTATIONS, ControlType.kPosition);
    }

    @Override
    public void outputTelemetry() {
        // TODO(Raina)
        ELEVATOR_SHUFFLEBOARD.putNumber("Left Output", left.getOutputCurrent());
        ELEVATOR_SHUFFLEBOARD.putNumber("Right Output", right.getOutputCurrent());
        ELEVATOR_SHUFFLEBOARD.putBoolean("Top Limit Hit", topLimit.get());
        ELEVATOR_SHUFFLEBOARD.putBoolean("Bottom Limit Hit", bottomLimit.get());
        ELEVATOR_SHUFFLEBOARD.putBoolean("Top Limit Enabled", topLimit.isLimitSwitchEnabled());
        ELEVATOR_SHUFFLEBOARD.putBoolean("Bottom Limit Enabled", bottomLimit.isLimitSwitchEnabled());
    }

    @Override
    public void stop() {
        left.stopMotor();
        right.stopMotor();
    }
}
