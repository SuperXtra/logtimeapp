package service

import data.{ChangeProjectName, CreateProject, DeleteProject, DeleteTask, Entities, LogTask, Queries, UpdateTask, UpdateTaskInsert}
import dbConnection.PostgresDb
import java.util.UUID

import akka.http.scaladsl.model.DateTime
import doobie.postgres._
import doobie.implicits._
import cats.effect.IO
import data.Entities._
import doobie.util.ExecutionContexts
import doobie.util.log.LogHandler
import error._
import cats.implicits._

class TaskService() {

  val con = PostgresDb.xa
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)


  def logTask(task: LogTask) = {

//        Queries.Project.getProjectId(task.projectName).map {
//          case Some(projectId) => {
//            Queries.User.getUserId(task.userIdentification).map {
//              case Some(userId) => for {
//                id <- Queries.Task.insert(task, projectId, userId).unique
//                task <- Queries.Task.selectLastInsertedTask(id).unique
//              } yield task.asRight
//              case None => CannotLogNewTaskWithDuplicateTaskDescriptionUnderTheSameProject.asLeft
//            }
//          }
//          case None => CannotLogNewTaskWithDuplicateTaskDescriptionUnderTheSameProject.asLeft
//        }.transact(con).attemptSomeSqlState {
//          case sqlstate.class23.EXCLUSION_VIOLATION => CannotLogNewTaskWithTheOverlappingTimeRangeForTheSameUser.asLeft
//          case sqlstate.class23.UNIQUE_VIOLATION => CannotLogNewTaskWithDuplicateTaskDescriptionUnderTheSameProject.asLeft
//        }
//      }


    val z = for {
      projectId <- Queries.Project.getProjectId(task.projectName).unique
      userId <- Queries.User.getUserId(task.userIdentification).unique
      id <- Queries.Task.insert(task, projectId, userId).unique
      task <- Queries.Task.selectLastInsertedTask(id).unique
    } yield task

    z.transact(con).attemptSomeSqlState {
      case sqlstate.class23.EXCLUSION_VIOLATION => CannotLogNewTaskWithTheOverlappingTimeRangeForTheSameUser
      case sqlstate.class23.UNIQUE_VIOLATION => CannotLogNewTaskWithDuplicateTaskDescriptionUnderTheSameProject
    }
  }

  def deleteTask(deleteTaskRequest: DeleteTask) = {
      val x = for {
        projectId <- Queries.Project.getProjectId(deleteTaskRequest.projectName).unique
        userId <- Queries.User.getUserId(deleteTaskRequest.userIdentification.toString).unique
        updatedCount <- Queries.Task.deleteTask(deleteTaskRequest.taskDescription, projectId, userId).run
      } yield updatedCount

    x.transact(con).attemptSomeSqlState {
      case x => s" error $x"
    }
  }

  def updateTask(updateTask: UpdateTask) = {

    def newTask(oldTask: Task, updateTask: UpdateTask) = {
      val newStartTime: String = updateTask.startTime getOrElse oldTask.startTime

      val newVolume: Option[Int] = updateTask.volume match {
        case Some(value: Int) => Some(value)
        case None => oldTask.volume match {
          case Some(value: Int) => Some(value)
          case None => None
        }
      }

      val comment = updateTask.comment match {
        case Some(value) => Some(value)
        case None => oldTask.comment match {
          case Some(value) => Some(value)
          case None => None
        }
      }


      UpdateTaskInsert(oldTask.projectId, oldTask.userId, updateTask.newTaskDescription,newStartTime, updateTask.durationTime, newVolume,comment)
    }

    val update = for {
      userId <- Queries.User.getUserId(updateTask.userIdentification).unique
      oldTask <- Queries.Task.fetchTask(updateTask.oldTaskDescription, userId).unique
      _ <- Queries.Task.deleteTask(oldTask.taskDescription, oldTask.projectId, oldTask.userId).run
      updated <- Queries.Task.insertUpdate(newTask(oldTask, updateTask)).unique
    } yield updated

    update.transact(con).attemptSomeSqlState {
      x => s"error: $x"
    }
  }
}
