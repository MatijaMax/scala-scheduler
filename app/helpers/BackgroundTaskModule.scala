package helpers

import com.google.inject.AbstractModule
import cats.effect.unsafe.IORuntime
import services.ReminderService

class BackgroundTaskModule extends AbstractModule {
  override def configure(): Unit = {
    // Bind IORuntime
    bind(classOf[IORuntime]).toInstance(IORuntime.global)

    // Bind EventReminder as an eager singleton
    bind(classOf[EventReminder]).asEagerSingleton()
  }
}
