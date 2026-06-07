package model;

import service.RegistrationService;

/**
 * 可報名行為介面 — 統一學生報名與取消的操作契約。
 */
public interface Registerable {
    String getParticipantId();
    RegistrationService.RegisterResult registerFor(Event event, RegistrationService service);
    void cancelRegistration(Event event, RegistrationService service);
}
