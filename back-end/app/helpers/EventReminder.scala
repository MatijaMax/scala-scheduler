package helpers

import cats.effect.unsafe.IORuntime
import javax.inject._
import play.api.inject.ApplicationLifecycle
import services.ReminderService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

@Singleton
class EventReminder @Inject() (
                                reminderService: ReminderService,
                                lifecycle: ApplicationLifecycle
                              )(implicit ec: ExecutionContext, runtime: IORuntime) {

  reminderService.runStream.compile.drain.unsafeRunAndForget()

  lifecycle.addStopHook { () =>
    Future.successful(())
  }
}

