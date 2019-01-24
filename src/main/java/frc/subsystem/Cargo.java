package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.loops.Loop;
import frc.loops.LooperInterface;
import frc.states.CargoState;
import frc.states.CargoStateMachine;
import frc.utils.Constants;

import static frc.utils.Constants.CARGO_SHUFFLEBOARD;

public class Cargo extends Subsystem {
    private static Cargo instance;

    // Hardware
    private final WPI_TalonSRX rearSide;
    private final WPI_TalonSRX leftFront;
    private final WPI_TalonSRX rightFront;
    private final DigitalInput cargoSensor = new DigitalInput(Constants.CARGO_SENSOR);
    private final WPI_TalonSRX intake;

    private CargoState currentState = new CargoState();
    private CargoStateMachine cargoStateMachine = new CargoStateMachine();

    private Cargo() {
        // TODO should we enable voltage compensation? That could ensure more consistent output and lower
        //  battery draw in general
        rearSide = new WPI_TalonSRX(Constants.CARGO_CENTER);
        leftFront = new WPI_TalonSRX(Constants.CARGO_LEFT);
        rightFront = new WPI_TalonSRX(Constants.CARGO_RIGHT);
        intake = new WPI_TalonSRX(Constants.INTAKE);
        rearSide.configVoltageCompSaturation(12.0, Constants.CAN_TIMEOUT_MS);
        leftFront.configVoltageCompSaturation(12.0, Constants.CAN_TIMEOUT_MS);
        rightFront.configVoltageCompSaturation(12.0, Constants.CAN_TIMEOUT_MS);
        intake.configVoltageCompSaturation(12.0, Constants.CAN_TIMEOUT_MS);


        // TODO make sure that the motors run the way that we expect, should be: +1 on
        //  left/right is intake and output, and +1 on rear moves it to the right when
        //  viewed from the back
        rightFront.setInverted(true);
    }

    public synchronized static Cargo getInstance() {
        if (instance == null) {
            instance = new Cargo();
        }
        return instance;
    }

    public synchronized void setDesiredState(CargoState.IntakeState intakeState) {
        cargoStateMachine.setDesiredState(intakeState);
    }

    @Override
    public void outputTelemetry() {
        CARGO_SHUFFLEBOARD.putString("Cargo Intake State", currentState.intakeState.toString());
        CARGO_SHUFFLEBOARD.putNumber("Rear Output", currentState.rearMotorOutput);
        CARGO_SHUFFLEBOARD.putNumber("Left Output", currentState.leftMotorOutput);
        CARGO_SHUFFLEBOARD.putNumber("Right Output", currentState.rightMotorOutput);
        CARGO_SHUFFLEBOARD.putNumber("Intake Output", currentState.intakeOutput);
    }

    @Override
    public synchronized void stop() {
        setDesiredState(CargoState.IntakeState.STOPPED);
    }

    @Override
    public void registerEnabledLoops(LooperInterface enabledLooper) {
        Loop loop = new Loop() {
            @Override
            public void onStart(double timestamp) {
                setDesiredState(CargoState.IntakeState.STOPPED);
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Cargo.this) {
                    CargoState newState = cargoStateMachine.onUpdate(getCargoState());
                    updateOutputFromState(newState);
                }
            }

            @Override
            public void onStop(double timestamp) {
                synchronized (Cargo.this) {
                    setDesiredState(CargoState.IntakeState.STOPPED);
                    stop();
                }
            }
        };

        enabledLooper.registerLoop(loop);
    }

    public synchronized boolean cargoInHold() {
        return currentState.ballInHold;
    }

    private synchronized CargoState getCargoState() {
        currentState.ballInHold = cargoSensor.get();
        return currentState;
    }

    private synchronized void updateOutputFromState(CargoState state) {
<<<<<<< HEAD
        rearSide.set(ControlMode.PercentOutput, state.rearMotor);
        leftFront.set(ControlMode.PercentOutput, state.leftMotor);
        rightFront.set(ControlMode.PercentOutput, state.rightMotor);
        intake.set(ControlMode.PercentOutput, state.intake);
    }}
    // set PID
=======
        rearSide.set(ControlMode.PercentOutput, state.rearMotorOutput);
        leftFront.set(ControlMode.PercentOutput, state.leftMotorOutput);
        rightFront.set(ControlMode.PercentOutput, state.rightMotorOutput);
        intake.set(ControlMode.PercentOutput, state.intakeOutput);
    }
}
>>>>>>> b282f76c36d7f51c815ca76b98027896241ce0a1
