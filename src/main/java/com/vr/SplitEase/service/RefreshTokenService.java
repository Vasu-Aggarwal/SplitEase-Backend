package com.vr.SplitEase.service;


import com.vr.SplitEase.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String uuid);
    RefreshToken verifyRefreshToken(String refreshToken);

}
