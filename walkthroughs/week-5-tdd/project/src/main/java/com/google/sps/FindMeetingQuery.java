// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Used to query through a list of events and attendees to find all times that can work for a meeting */
public final class FindMeetingQuery {
  
  /** Returns a list of possible times the meeting can be held- includes optional attendees if possible. */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = new ArrayList<String>(request.getAttendees());
    Collection<String> optionalAttendees = new ArrayList<String>(request.getOptionalAttendees());
    long duration = request.getDuration();

    // if duration is longer than the entire day, no meeting can occur
    if (duration > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }
    // no attendees or optional attendees
    if (attendees.isEmpty() && optionalAttendees.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    // only mandatory attendees
    if (!attendees.isEmpty() && optionalAttendees.isEmpty()){
      return findTimes(attendees, events, duration);
    }
    // only optional attendees
    if (attendees.isEmpty() && !optionalAttendees.isEmpty()){
      return findTimes(optionalAttendees, events, duration);
    }
    // both optional and mandatory attendees, try merging the two attendee lists together and finding meeting times.
    if (!attendees.isEmpty() && !optionalAttendees.isEmpty()){
      List<String> combinedAttendees = new ArrayList<String>();
      combinedAttendees.addAll(attendees);
      combinedAttendees.addAll(optionalAttendees);
      Collection<TimeRange> toReturn = findTimes(combinedAttendees, events, duration);
      if (toReturn.size() > 0) {
        return toReturn;
      }
    }
    // if no meeting time exists such that mandatory and optional attendees can all go, just find meeting times for mandatory attendees
    return findTimes(attendees, events, duration);
  }

  /** Given a list of attendees and events, returns meeting times that work given the necessary duration. */
  private Collection<TimeRange> findTimes(Collection<String> attendees, Collection<Event> events, long duration) {
    List<TimeRange> conflicts = new ArrayList<TimeRange>();
    Collection<TimeRange> toReturn = new ArrayList<TimeRange>();
 
    for (Event event : events) {
      if (containsAttendees(attendees, event.getAttendees())) {
        conflicts.add(event.getWhen());
      }
    }

    if (!conflicts.isEmpty()) {
      toReturn = findNonConflictingTimes(conflicts, (int) duration);
      return toReturn;
    }

    return Arrays.asList(TimeRange.WHOLE_DAY);
  }

  /** Given a list of conflicts, invert the conflict times to find available times given the necessary duration of the meeting */
  private Collection<TimeRange> findNonConflictingTimes(List<TimeRange> conflicts, int duration) {
    Collection<TimeRange> toReturn = new ArrayList<TimeRange>();
    int start = TimeRange.START_OF_DAY;
    int end = TimeRange.END_OF_DAY;
    Collections.sort(conflicts, TimeRange.ORDER_BY_START);
    for (int i = 0; i < conflicts.size(); i++) {
      TimeRange conflict = conflicts.get(i);
 
      if (conflict.start() > start && conflict.start() - start >= duration && start + conflict.start() <= end) {
        TimeRange toAdd = TimeRange.fromStartDuration(start, conflict.start()-start);
        toReturn.add(toAdd);
        start = conflict.end();
      }
      else if (conflict.start() <= start && conflict.end() < start) {
        continue;
      }
 
      start = conflict.end();
    }
    if (end-start >= duration) {
      TimeRange toAdd = TimeRange.fromStartDuration(start, end-start +1);
      toReturn.add(toAdd);
    }
 
    return toReturn;
  }

  /** Returns true if the two lists contain some of the same attendees */
  private Boolean containsAttendees(Collection<String> actualAttendees, Collection<String> eventAttendees) {
    return !Collections.disjoint(actualAttendees, eventAttendees);
  }

}
