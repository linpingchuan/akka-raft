package pl.project13.scala.akka.raft

import pl.project13.scala.akka.raft.protocol._

class LeaderElectionTest extends RaftSpec(callingThreadDispatcher = false) {

  behavior of "Leader Election"

  val memberCount = 5

  // note: these run sequential, so when the 2 test runs, we already have a leader,
  // so we can kill it, and see it the cluster re-elects a new one properly

  it should "elect initial Leader" in {
    // given
    subscribeElectedLeader()

    info("Before election: ")
    infoMemberStates()

    // when
    awaitElectedLeader()
    info("After election: ")
    infoMemberStates()

    // then
    val leaderCount = members.count(_.stateName == Leader)
    val candidateCount = members.count(_.stateName == Candidate)
    val followerCount = members.count(_.stateName == Follower)

    leaderCount should equal (1)
    (candidateCount + followerCount) should equal (4)
  }

  it should "elect replacement Leader if current Leader dies" in {
    // given
    subscribeElectedLeader()

    infoMemberStates()

    // when
    killLeader()

    // then
    awaitElectedLeader()
    info("New leader elected: ")
    infoMemberStates()

    val leaderCount = members.count(_.stateName == Leader)
      val candidateCount = members.count(_.stateName == Candidate)
      val followerCount = members.count(_.stateName == Follower)

      leaderCount should equal (1)
      (candidateCount + followerCount) should equal (4)
  }

  it should "be able to maintain the same leader for a long time" in {
    // given
    subscribeElectedLeader()

    // when
    val memberStates1 = members.sortBy(_.path.elements.last).map(_.stateName)
    Thread.sleep(50)
    val memberStates2 = members.sortBy(_.path.elements.last).map(_.stateName)
    Thread.sleep(50)
    val memberStates3 = members.sortBy(_.path.elements.last).map(_.stateName)
    Thread.sleep(50)
    val memberStates4 = members.sortBy(_.path.elements.last).map(_.stateName)

    info("Maintained state:")
    infoMemberStates()

    // then
    memberStates1 should equal (memberStates2)
    memberStates1 should equal (memberStates3)
    memberStates1 should equal (memberStates4)
  }

}
