
package ru.sortlab.visualization;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Consumer;

/**
 * SortAnimationPlayer — проигрыватель кадров.
 *
 * Он ничего не знает об алгоритмах сортировки.
 *
 * Получает готовые кадры и передаёт
 * их интерфейсу по одному.
 */
public class SortAnimationPlayer {

    private Timeline timeline;

    private int currentFrameIndex;

    private List<SortFrame> frames;

    /**
     * Запускает воспроизведение.
     *
     * @param frames кадры анимации
     * @param delayMillis задержка между кадрами
     * @param frameConsumer обработчик кадра
     */
    public void play(List<SortFrame> frames, double delayMillis, Consumer<SortFrame> frameConsumer,
                     Runnable onFinished) {

        // Останавливаем предыдущую анимацию.
        stop();

        this.frames = frames;
        this.currentFrameIndex = 0;

        if (frames.isEmpty()) {
            onFinished.run();
            return;
        }

        // Показываем начальный кадр.
        frameConsumer.accept(frames.get(0));

        timeline = new Timeline(new KeyFrame(Duration.millis(delayMillis), event -> {

                            currentFrameIndex++;

                            // Проверяем завершение анимации.
                            if (currentFrameIndex >= frames.size()) {

                                stop();

                                // Уведомляем контроллер о завершении.
                                onFinished.run();

                                return;
                            }

                            // Показываем следующий кадр.
                            frameConsumer.accept(
                                    frames.get(currentFrameIndex)
                            );
                        }
                )
        );

        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.play();
    }

    /**
     * Приостанавливает воспроизведение.
     */
    public void pause() {

        if (timeline != null) {
            timeline.pause();
        }
    }

    /**
     * Продолжает воспроизведение.
     */
    public void resume() {

        if (timeline != null) {
            timeline.play();
        }
    }

    /**
     * Полностью останавливает анимацию.
     */
    public void stop() {

        if (timeline != null) {
            timeline.stop();
        }
    }
}
