package com.test

import coursier._
import coursier.core.Authentication

object CoursierTest extends App {
  val start = Resolution(
    Set(
      Dependency(
        Module("com.wix.pay", "credit-card"), "1.3.0"
      )
    )
  )

  val repositories = Seq(
    MavenRepository(
      "http://repo.dev.wix/artifactory/libs-releases-local",
      authentication = Some(Authentication("ci", "wixpress"))
    )
  )

  val fetch = Fetch.from(repositories, Cache.fetch())

  val resolution = start.process.run(fetch).run

  import java.io.File
  import scalaz.\/
  import scalaz.concurrent.Task

  val localArtifacts: Seq[FileError \/ File] = Task.gatherUnordered(
    resolution.artifacts.map(Cache.file(_).run)
  ).run

  println("localArtifacts: " + localArtifacts)
}
