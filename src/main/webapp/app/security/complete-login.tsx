import React, { useEffect } from 'react';
import { useSearchParams } from 'react-router';

export default function CompleteLogin() {
    const [searchParams] = useSearchParams();

    useEffect(() => {
        // This page is loaded in the popup window
        // The parent window (authentication.tsx) will detect this and extract the code
        // We just display a message to the user
        const code = searchParams.get('code');
        const state = searchParams.get('state');

        if (code && state) {
            // The popup will be closed by the parent window's checkPopup function
            console.log('OAuth callback received with code and state');
        }
    }, [searchParams]);

    return (
        <div className="text-center mt-5">
            <h2>Completing login...</h2>
            <p>Please wait while we complete your login. This window will close automatically.</p>
        </div>
    );
}