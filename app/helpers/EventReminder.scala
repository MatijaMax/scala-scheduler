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

  // The stream stars here
  reminderService.runStream.compile.drain.unsafeRunAndForget()

  // Clean up on stop
  lifecycle.addStopHook { () =>
    Future.successful(())
  }
}

