package helpers

import com.google.inject.AbstractModule
import cats.effect.unsafe.IORuntime
import services.ReminderService

class BackgroundTaskModule extends AbstractModule {
  override def configure(): Unit = {

    bind(classOf[IORuntime]).toInstance(IORuntime.global)

    bind(classOf[EventReminder]).asEagerSingleton()
  }
}
