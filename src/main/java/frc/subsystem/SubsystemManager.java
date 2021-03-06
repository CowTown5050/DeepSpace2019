package frc.subsystem;

import edu.wpi.first.wpilibj.DriverStation;
import frc.loops.Loop;
import frc.loops.Looper;
import frc.loops.LooperInterface;

import java.util.ArrayList;
import java.util.List;

public class SubsystemManager implements LooperInterface {
    private final List<Subsystem> subsystems;

    private final List<Loop> loops = new ArrayList<>();

    public SubsystemManager(List<Subsystem> subsystems) {
        this.subsystems = subsystems;
    }

    public void stop() {
        for (Subsystem subsystem : subsystems) {
            subsystem.stop();
        }
    }

    public void outputTelemetry() {
        for (Subsystem subsystem : subsystems) {
            subsystem.outputTelemetry();
        }
    }

    public void registerEnabledLoops(Looper looper) {
        for (Subsystem s : subsystems) {
            s.registerEnabledLoops(this);
        }
        looper.registerLoop(new EnabledLoop());
    }

    public void registerDisabledLoop(Looper disabledLooper) {
        disabledLooper.registerLoop(new DisabledLoop());
    }

    @Override
    public void registerLoop(Loop loop) {
        if (loop == null) {
            DriverStation.reportWarning("Loop null", true);
        } else {
            loops.add(loop);
        }
    }

    private class EnabledLoop implements Loop {

        @Override
        public void onStart(double timestamp) {
            for (Loop loop : loops) {
                loop.onStart(timestamp);
            }
        }

        @Override
        public void onLoop(double timestamp) {
            for (Subsystem subsystem : subsystems) {
                subsystem.readPeriodicInputs();
            }
            for (Loop loop : loops) {
                loop.onLoop(timestamp);
            }
            for (Subsystem subsystem : subsystems) {
                subsystem.writePeriodicOutputs();
            }
        }

        @Override
        public void onStop(double timestamp) {
            for (Loop loop : loops) {
                loop.onStop(timestamp);
            }
        }
    }

    private class DisabledLoop implements Loop {

        @Override
        public void onStart(double timestamp) {

        }

        @Override
        public void onLoop(double timestamp) {
            for (Subsystem subsystem : subsystems) {
                subsystem.readPeriodicInputs();
            }
            for (Subsystem subsystem : subsystems) {
                subsystem.writePeriodicOutputs();
            }
        }

        @Override
        public void onStop(double timestamp) {

        }
    }
}